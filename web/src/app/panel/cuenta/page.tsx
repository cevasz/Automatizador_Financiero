import { createClient } from '@/lib/supabase/server';
import { formatDate } from '@/lib/format';
import BorrarTodo from './BorrarTodo';

export const metadata = { title: 'Cuenta — Kivo' };

export default async function CuentaPage() {
  const supabase = await createClient();
  const {
    data: { user }
  } = await supabase.auth.getUser();

  const tablas = [
    ['movements', 'Movimientos'],
    ['categories', 'Categorías'],
    ['agenda_entries', 'Agenda'],
    ['budgets', 'Presupuestos'],
    ['savings_goals', 'Metas'],
    ['classification_rules', 'Reglas de clasificación'],
    ['invoices', 'Facturas']
  ] as const;

  const conteos = await Promise.all(
    tablas.map(([tabla]) =>
      supabase.from(tabla).select('id', { count: 'exact', head: true }).eq('deleted', false)
    )
  );

  // Ultima sincronizacion = el synced_at mas reciente de movimientos, que es lo
  // que el telefono sube en cada pasada.
  const { data: ultimo } = await supabase
    .from('movements')
    .select('synced_at')
    .order('synced_at', { ascending: false })
    .limit(1)
    .maybeSingle();

  return (
    <div className="stack" style={{ gap: '1.5rem' }}>
      <header className="stack" style={{ gap: '0.25rem' }}>
        <p className="eyebrow">{user?.email}</p>
        <h1>Tu cuenta</h1>
      </header>

      <section className="card stack">
        <h2>Qué hay sincronizado</h2>
        <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))' }}>
          {tablas.map(([, etiqueta], i) => (
            <div key={etiqueta} className="stack" style={{ gap: '0.1rem' }}>
              <strong className="numero" style={{ fontSize: '1.35rem' }}>
                {conteos[i].count ?? 0}
              </strong>
              <span className="tiny muted">{etiqueta}</span>
            </div>
          ))}
        </div>
        <p className="small muted" style={{ margin: 0 }}>
          {ultimo?.synced_at
            ? `Última sincronización desde el teléfono: ${formatDate(ultimo.synced_at as string)}.`
            : 'Todavía no ha llegado ninguna sincronización desde el teléfono.'}
        </p>
      </section>

      <section className="card stack">
        <h2>Llevarte tus datos</h2>
        <p className="small muted" style={{ margin: 0 }}>
          La Ley 1581 de 2012 te da derecho a obtener una copia de tu información en
          cualquier momento. No hay que pedirla ni esperar.
        </p>
        <div className="row wrap" style={{ gap: '0.6rem' }}>
          <a className="btn" href="/api/exportar?formato=csv">
            Movimientos en CSV
          </a>
          <a className="btn btn--ghost" href="/api/exportar?formato=json">
            Todo en JSON
          </a>
        </div>
      </section>

      <section className="card stack">
        <h2>Borrar tus datos de la nube</h2>
        <p className="small muted" style={{ margin: 0 }}>
          Elimina de forma permanente todo lo que hay en el servidor: movimientos,
          categorías, agenda, presupuestos, metas y facturas.{' '}
          <strong>Lo que está en tu teléfono no se toca</strong> — Kivo funciona sin nube.
          Ojo: si vuelves a sincronizar desde la app, los datos locales se subirán otra vez.
        </p>
        <BorrarTodo />
      </section>
    </div>
  );
}
