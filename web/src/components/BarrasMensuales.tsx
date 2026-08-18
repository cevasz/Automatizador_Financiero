import { formatCents, formatMonth } from '@/lib/format';
import type { MonthlySummaryRow } from '@/lib/types';

/**
 * Grafica de barras mensuales dibujada con CSS, sin libreria de charts: son
 * doce pares de barras: traer un motor de graficas entero para esto agrandaria
 * el bundle mucho mas de lo que aporta.
 */
export default function BarrasMensuales({ filas }: { filas: MonthlySummaryRow[] }) {
  const meses = [...new Set(filas.map((f) => f.mes))].sort();

  const datos = meses.map((mes) => ({
    mes,
    ingreso: filas.find((f) => f.mes === mes && f.type === 'INCOME')?.total ?? 0,
    gasto: filas.find((f) => f.mes === mes && f.type === 'EXPENSE')?.total ?? 0
  }));

  // Escala compartida entre ingresos y gastos: con escalas independientes, un
  // gasto pequeño se veria igual de alto que un ingreso grande.
  const tope = Math.max(1, ...datos.flatMap((d) => [d.ingreso, d.gasto]));

  return (
    <div className="tabla-scroll">
      <div
        style={{
          display: 'flex',
          alignItems: 'flex-end',
          gap: '0.9rem',
          minHeight: 180,
          paddingTop: '0.5rem'
        }}
      >
        {datos.map((d) => (
          <div key={d.mes} style={{ flex: '1 0 44px', textAlign: 'center' }}>
            <div
              style={{ display: 'flex', alignItems: 'flex-end', gap: 3, height: 140 }}
              role="img"
              aria-label={`${formatMonth(d.mes)}: ingresos ${formatCents(d.ingreso)}, gastos ${formatCents(d.gasto)}`}
            >
              <Barra alto={(d.ingreso / tope) * 100} color="var(--ingreso)" />
              <Barra alto={(d.gasto / tope) * 100} color="var(--gasto)" />
            </div>
            <span className="tiny muted" style={{ display: 'block', marginTop: '0.4rem' }}>
              {formatMonth(d.mes)}
            </span>
          </div>
        ))}
      </div>

      <div className="row small muted" style={{ gap: '1rem', marginTop: '0.75rem' }}>
        <span className="row" style={{ gap: '0.35rem' }}>
          <i style={{ width: 10, height: 10, borderRadius: 3, background: 'var(--ingreso)' }} /> Ingresos
        </span>
        <span className="row" style={{ gap: '0.35rem' }}>
          <i style={{ width: 10, height: 10, borderRadius: 3, background: 'var(--gasto)' }} /> Gastos
        </span>
      </div>
    </div>
  );
}

function Barra({ alto, color }: { alto: number; color: string }) {
  return (
    <span
      style={{
        flex: 1,
        // Minimo de 2px: un mes con un movimiento pequeño quedaria invisible y
        // se leeria como "no hubo nada", que es distinto.
        height: `${Math.max(2, alto)}%`,
        background: color,
        borderRadius: '4px 4px 0 0',
        display: 'block'
      }}
    />
  );
}
