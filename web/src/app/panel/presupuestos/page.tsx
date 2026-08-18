import { createClient } from '@/lib/supabase/server';
import { formatCents, percent, startOfCurrentMonth } from '@/lib/format';
import type { Budget, Category, Movement } from '@/lib/types';
import { ErrorDeCarga } from '../page';
import EditorPresupuesto from './EditorPresupuesto';
import { borrarPresupuesto } from '../actions';
import BotonBorrar from '@/components/BotonBorrar';

export const metadata = { title: 'Presupuestos — Kivo' };

export default async function PresupuestosPage() {
  const supabase = await createClient();
  const hoy = new Date();
  const mes = hoy.getMonth() + 1;
  const anio = hoy.getFullYear();

  const [presupuestos, categorias, gastosDelMes] = await Promise.all([
    supabase.from('budgets').select('*').eq('deleted', false).eq('month', mes).eq('year', anio),
    supabase.from('categories').select('*').eq('deleted', false).eq('type', 'EXPENSE').order('name'),
    supabase
      .from('movements')
      .select('category_id, amount')
      .eq('deleted', false)
      .eq('type', 'EXPENSE')
      .neq('confirmation_state', 'REJECTED')
      .gte('date', startOfCurrentMonth())
  ]);

  if (presupuestos.error) return <ErrorDeCarga mensaje={presupuestos.error.message} />;

  const cats = (categorias.data ?? []) as Category[];
  const catPorId = new Map(cats.map((c) => [c.id, c]));

  // Gasto real por categoria: se suma aqui y no en SQL porque son los
  // movimientos de un solo mes de una sola persona — unos cientos de filas.
  const gastado = new Map<string, number>();
  for (const m of (gastosDelMes.data ?? []) as Pick<Movement, 'category_id' | 'amount'>[]) {
    if (!m.category_id) continue;
    gastado.set(m.category_id, (gastado.get(m.category_id) ?? 0) + m.amount);
  }

  const filas = ((presupuestos.data ?? []) as Budget[]).sort((a, b) =>
    (catPorId.get(a.category_id)?.name ?? '').localeCompare(catPorId.get(b.category_id)?.name ?? '')
  );

  return (
    <div className="stack" style={{ gap: '1.5rem' }}>
      <header className="stack" style={{ gap: '0.25rem' }}>
        <p className="eyebrow">
          {new Intl.DateTimeFormat('es-CO', { month: 'long', year: 'numeric' }).format(hoy)}
        </p>
        <h1>Presupuestos</h1>
      </header>

      <EditorPresupuesto categorias={cats} mes={mes} anio={anio} />

      {filas.length === 0 ? (
        <div className="card vacio">
          <p style={{ margin: 0 }}>Aún no hay presupuestos para este mes.</p>
        </div>
      ) : (
        <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))' }}>
          {filas.map((b) => {
            const usado = gastado.get(b.category_id) ?? 0;
            const pct = percent(usado, b.monthly_limit);
            const clase = pct >= 100 ? 'progreso progreso--excedido' : pct >= 80 ? 'progreso progreso--alerta' : 'progreso';

            return (
              <article key={b.id} className="card stack" style={{ gap: '0.6rem' }}>
                <div className="row row--between">
                  <h3>{catPorId.get(b.category_id)?.name ?? 'Categoría eliminada'}</h3>
                  <span className="tag">{pct}%</span>
                </div>

                <div className={clase}>
                  <span style={{ width: `${pct}%` }} />
                </div>

                <div className="row row--between small">
                  <span className="numero">{formatCents(usado)}</span>
                  <span className="muted numero">de {formatCents(b.monthly_limit)}</span>
                </div>

                {usado > b.monthly_limit && (
                  <span className="tag tag--gasto">
                    Excedido en {formatCents(usado - b.monthly_limit)}
                  </span>
                )}

                <div className="row" style={{ gap: '0.5rem' }}>
                  <EditorPresupuesto
                    categorias={cats}
                    mes={mes}
                    anio={anio}
                    existente={b}
                    compacto
                  />
                  <BotonBorrar
                    accion={borrarPresupuesto.bind(null, b.id)}
                    confirmacion="¿Eliminar este presupuesto?"
                  />
                </div>
              </article>
            );
          })}
        </div>
      )}
    </div>
  );
}
