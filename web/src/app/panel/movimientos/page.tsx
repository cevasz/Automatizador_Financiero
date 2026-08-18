import { createClient } from '@/lib/supabase/server';
import { BANK_LABELS, type Category, type Movement } from '@/lib/types';
import { ErrorDeCarga } from '../page';
import FiltrosMovimientos from './FiltrosMovimientos';
import TablaMovimientos from './TablaMovimientos';

export const metadata = { title: 'Movimientos — Kivo' };

const POR_PAGINA = 50;

export default async function MovimientosPage({
  searchParams
}: {
  searchParams: Promise<Record<string, string | string[] | undefined>>;
}) {
  const params = await searchParams;
  const uno = (k: string) => (Array.isArray(params[k]) ? params[k][0] : params[k]) ?? '';

  const estado = uno('estado');
  const banco = uno('banco');
  const categoria = uno('categoria');
  const busqueda = uno('q').trim();
  const pagina = Math.max(1, Number(uno('pagina')) || 1);

  const supabase = await createClient();

  let consulta = supabase
    .from('movements')
    .select('*', { count: 'exact' })
    .eq('deleted', false)
    .order('date', { ascending: false })
    .range((pagina - 1) * POR_PAGINA, pagina * POR_PAGINA - 1);

  if (estado) consulta = consulta.eq('confirmation_state', estado);
  if (banco) consulta = consulta.eq('bank_entity', banco);
  if (categoria === 'sin') consulta = consulta.is('category_id', null);
  else if (categoria) consulta = consulta.eq('category_id', categoria);
  // ilike y no eq: el usuario busca "rappi", no el texto exacto guardado.
  if (busqueda) consulta = consulta.ilike('counterparty_raw', `%${busqueda}%`);

  const [movimientos, categorias] = await Promise.all([
    consulta,
    supabase.from('categories').select('*').eq('deleted', false).order('name')
  ]);

  if (movimientos.error) return <ErrorDeCarga mensaje={movimientos.error.message} />;

  const filas = (movimientos.data ?? []) as Movement[];
  const cats = (categorias.data ?? []) as Category[];
  const total = movimientos.count ?? 0;
  const paginas = Math.max(1, Math.ceil(total / POR_PAGINA));

  return (
    <div className="stack" style={{ gap: '1.5rem' }}>
      <header className="stack" style={{ gap: '0.25rem' }}>
        <p className="eyebrow">{total} movimiento{total === 1 ? '' : 's'}</p>
        <h1>Movimientos</h1>
        <p className="muted small" style={{ margin: 0 }}>
          Puedes corregir la categoría y confirmar o rechazar desde aquí. El cambio llega
          a tu teléfono en la siguiente sincronización.
        </p>
      </header>

      <FiltrosMovimientos
        categorias={cats}
        bancos={Object.keys(BANK_LABELS)}
        valores={{ estado, banco, categoria, q: busqueda }}
      />

      <TablaMovimientos movimientos={filas} categorias={cats} />

      {paginas > 1 && (
        <nav className="row" style={{ justifyContent: 'center', gap: '0.75rem' }}>
          <Paginacion pagina={pagina} paginas={paginas} params={params} />
        </nav>
      )}
    </div>
  );
}

function Paginacion({
  pagina,
  paginas,
  params
}: {
  pagina: number;
  paginas: number;
  params: Record<string, string | string[] | undefined>;
}) {
  const enlace = (p: number) => {
    const qs = new URLSearchParams();
    for (const [k, v] of Object.entries(params)) {
      if (k !== 'pagina' && typeof v === 'string' && v) qs.set(k, v);
    }
    qs.set('pagina', String(p));
    return `/panel/movimientos?${qs}`;
  };

  return (
    <>
      <a
        className={`btn btn--ghost btn--sm ${pagina <= 1 ? 'btn--desactivado' : ''}`}
        href={pagina <= 1 ? undefined : enlace(pagina - 1)}
        aria-disabled={pagina <= 1}
        style={pagina <= 1 ? { opacity: 0.4, pointerEvents: 'none' } : undefined}
      >
        ← Anterior
      </a>
      <span className="small muted">
        Página {pagina} de {paginas}
      </span>
      <a
        className="btn btn--ghost btn--sm"
        href={pagina >= paginas ? undefined : enlace(pagina + 1)}
        aria-disabled={pagina >= paginas}
        style={pagina >= paginas ? { opacity: 0.4, pointerEvents: 'none' } : undefined}
      >
        Siguiente →
      </a>
    </>
  );
}
