import Link from 'next/link';
import { createClient } from '@/lib/supabase/server';
import { formatCents, formatDateShort, formatMonth, startOfCurrentMonth } from '@/lib/format';
import { BANK_LABELS, type Category, type Movement, type MonthlySummaryRow } from '@/lib/types';
import BarrasMensuales from '@/components/BarrasMensuales';

export const metadata = { title: 'Inicio — Kivo' };

export default async function DashboardPage() {
  const supabase = await createClient();
  const desde = startOfCurrentMonth();

  // Cinco consultas en paralelo. En serie, cada una esperaria a la anterior sin
  // ninguna razon: no dependen entre si.
  const [mesActual, pendientes, recientes, categorias, resumen] = await Promise.all([
    supabase
      .from('movements')
      .select('type, amount')
      .eq('deleted', false)
      .neq('confirmation_state', 'REJECTED')
      .gte('date', desde),
    supabase
      .from('movements')
      .select('id', { count: 'exact', head: true })
      .eq('deleted', false)
      .eq('confirmation_state', 'PENDING'),
    supabase
      .from('movements')
      .select('*')
      .eq('deleted', false)
      .order('date', { ascending: false })
      .limit(8),
    supabase.from('categories').select('*').eq('deleted', false),
    supabase
      .from('movement_monthly_summary')
      .select('*')
      .order('mes', { ascending: true })
      .limit(24)
  ]);

  const error = mesActual.error ?? recientes.error ?? categorias.error;
  if (error) return <ErrorDeCarga mensaje={error.message} />;

  const filas = (mesActual.data ?? []) as Pick<Movement, 'type' | 'amount'>[];
  const ingresos = filas.filter((m) => m.type === 'INCOME').reduce((a, m) => a + m.amount, 0);
  const gastos = filas.filter((m) => m.type === 'EXPENSE').reduce((a, m) => a + m.amount, 0);

  const catPorId = new Map(((categorias.data ?? []) as Category[]).map((c) => [c.id, c]));
  const ultimos = (recientes.data ?? []) as Movement[];
  const serie = ((resumen.data ?? []) as MonthlySummaryRow[]).slice(-12);

  return (
    <div className="stack" style={{ gap: '1.75rem' }}>
      <header className="stack" style={{ gap: '0.25rem' }}>
        <p className="eyebrow">Resumen de {formatMonth(desde)}</p>
        <h1>Cómo va tu mes</h1>
      </header>

      <section className="grid">
        <Metrica etiqueta="Ingresos" valor={formatCents(ingresos)} color="var(--ingreso)" />
        <Metrica etiqueta="Gastos" valor={formatCents(gastos)} color="var(--gasto)" />
        <Metrica
          etiqueta="Balance"
          valor={formatCents(ingresos - gastos)}
          color={ingresos - gastos >= 0 ? 'var(--ingreso)' : 'var(--gasto)'}
        />
        <Metrica
          etiqueta="Por confirmar"
          valor={String(pendientes.count ?? 0)}
          color="var(--aviso)"
          pie={
            (pendientes.count ?? 0) > 0 ? (
              <Link className="tiny" href="/panel/movimientos?estado=PENDING" style={{ color: 'var(--barro)' }}>
                Revisarlos →
              </Link>
            ) : (
              <span className="tiny muted">Todo al día</span>
            )
          }
        />
      </section>

      {serie.length > 0 && (
        <section className="card stack">
          <h2>Últimos meses</h2>
          <BarrasMensuales filas={serie} />
        </section>
      )}

      <section className="card stack">
        <div className="row row--between">
          <h2>Movimientos recientes</h2>
          <Link className="small" href="/panel/movimientos" style={{ color: 'var(--barro)' }}>
            Ver todos →
          </Link>
        </div>

        {ultimos.length === 0 ? (
          <div className="vacio">
            <p style={{ margin: 0 }}>Todavía no hay movimientos sincronizados.</p>
            <p className="small" style={{ marginBottom: 0 }}>
              Abre Kivo en tu teléfono y toca <strong>Sincronizar</strong> en la pantalla de Cuenta.
            </p>
          </div>
        ) : (
          <div className="tabla-scroll">
            <table className="tabla">
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Contraparte</th>
                  <th>Categoría</th>
                  <th>Banco</th>
                  <th className="num">Monto</th>
                </tr>
              </thead>
              <tbody>
                {ultimos.map((m) => (
                  <tr key={m.id}>
                    <td className="small muted">{formatDateShort(m.date)}</td>
                    <td>{m.counterparty_raw || '—'}</td>
                    <td className="small">
                      {m.category_id ? (
                        catPorId.get(m.category_id)?.name ?? 'Categoría eliminada'
                      ) : (
                        <span className="tag tag--aviso">Sin clasificar</span>
                      )}
                    </td>
                    <td className="small muted">{BANK_LABELS[m.bank_entity] ?? m.bank_entity}</td>
                    <td
                      className="num"
                      style={{ color: m.type === 'INCOME' ? 'var(--ingreso)' : 'var(--gasto)' }}
                    >
                      {m.type === 'INCOME' ? '+' : '−'} {formatCents(m.amount)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}

function Metrica({
  etiqueta,
  valor,
  color,
  pie
}: {
  etiqueta: string;
  valor: string;
  color: string;
  pie?: React.ReactNode;
}) {
  return (
    <div className="card stack" style={{ gap: '0.35rem' }}>
      <span className="eyebrow">{etiqueta}</span>
      <strong className="numero" style={{ fontSize: '1.55rem', color }}>
        {valor}
      </strong>
      {pie}
    </div>
  );
}

export function ErrorDeCarga({ mensaje }: { mensaje: string }) {
  return (
    <div className="card stack">
      <h2>No se pudieron cargar los datos</h2>
      <p className="small muted" style={{ margin: 0 }}>
        {mensaje}
      </p>
      <p className="tiny muted" style={{ margin: 0 }}>
        Si es la primera vez que abres el panel, revisa que las migraciones de
        <code> backend/supabase/migrations/</code> se hayan ejecutado en el proyecto.
      </p>
    </div>
  );
}
