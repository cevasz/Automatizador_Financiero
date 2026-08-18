'use client';

import { useState, useTransition } from 'react';
import { actualizarMovimiento, borrarMovimiento } from '../actions';
import { formatCents, formatDate } from '@/lib/format';
import {
  BANK_LABELS,
  SOURCE_LABELS,
  STATE_LABELS,
  type Category,
  type ConfirmationState,
  type Movement
} from '@/lib/types';

export default function TablaMovimientos({
  movimientos,
  categorias
}: {
  movimientos: Movement[];
  categorias: Category[];
}) {
  if (movimientos.length === 0) {
    return (
      <div className="card vacio">
        <p style={{ margin: 0 }}>Ningún movimiento coincide con estos filtros.</p>
      </div>
    );
  }

  return (
    <div className="card" style={{ padding: '1rem' }}>
      <div className="tabla-scroll">
        <table className="tabla" style={{ minWidth: 860 }}>
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Contraparte</th>
              <th>Categoría</th>
              <th>Estado</th>
              <th className="num">Monto</th>
              <th aria-label="Acciones" />
            </tr>
          </thead>
          <tbody>
            {movimientos.map((m) => (
              <Fila key={m.id} movimiento={m} categorias={categorias} />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function Fila({ movimiento, categorias }: { movimiento: Movement; categorias: Category[] }) {
  const [pendiente, startTransition] = useTransition();
  const [error, setError] = useState<string | null>(null);
  const [abierto, setAbierto] = useState(false);

  // Solo se ofrecen categorias del mismo tipo: poner "Salario" (ingreso) en un
  // gasto rompe presupuestos y graficas sin que se note.
  const compatibles = categorias.filter((c) => c.type === movimiento.type);

  function ejecutar(accion: () => Promise<{ ok: boolean; error?: string }>) {
    setError(null);
    startTransition(async () => {
      const r = await accion();
      if (!r.ok) setError(r.error ?? 'No se pudo guardar.');
    });
  }

  return (
    <>
      <tr style={{ opacity: pendiente ? 0.55 : 1 }}>
        <td className="small muted" style={{ whiteSpace: 'nowrap' }}>
          {formatDate(movimiento.date)}
        </td>

        <td>
          <button
            className="row"
            onClick={() => setAbierto((v) => !v)}
            aria-expanded={abierto}
            style={{
              background: 'none',
              border: 'none',
              padding: 0,
              cursor: 'pointer',
              textAlign: 'left',
              gap: '0.4rem'
            }}
          >
            <span>{movimiento.counterparty_raw || '—'}</span>
            <span className="tiny muted">{abierto ? '▴' : '▾'}</span>
          </button>
          <div className="tiny muted">{BANK_LABELS[movimiento.bank_entity] ?? movimiento.bank_entity}</div>
        </td>

        <td>
          <select
            className="input"
            style={{ minWidth: 160, padding: '0.3rem 0.5rem', fontSize: '0.85rem' }}
            value={movimiento.category_id ?? ''}
            disabled={pendiente}
            onChange={(e) =>
              ejecutar(() =>
                actualizarMovimiento(movimiento.id, { category_id: e.target.value || null })
              )
            }
          >
            <option value="">Sin clasificar</option>
            {compatibles.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </td>

        <td>
          <EstadoCelda
            estado={movimiento.confirmation_state}
            deshabilitado={pendiente}
            onCambio={(estado) => ejecutar(() => actualizarMovimiento(movimiento.id, { confirmation_state: estado }))}
          />
        </td>

        <td
          className="num"
          style={{ color: movimiento.type === 'INCOME' ? 'var(--ingreso)' : 'var(--gasto)', fontWeight: 600 }}
        >
          {movimiento.type === 'INCOME' ? '+' : '−'} {formatCents(movimiento.amount)}
        </td>

        <td>
          <button
            className="btn btn--danger btn--sm"
            disabled={pendiente}
            onClick={() => {
              if (confirm('¿Eliminar este movimiento? También desaparecerá del teléfono.')) {
                ejecutar(() => borrarMovimiento(movimiento.id));
              }
            }}
          >
            Eliminar
          </button>
        </td>
      </tr>

      {(abierto || error) && (
        <tr>
          <td colSpan={6} style={{ background: 'var(--superficie-alt)', borderRadius: 'var(--radio-sm)' }}>
            {error && (
              <p className="small" role="alert" style={{ color: 'var(--barro)', margin: '0 0 0.5rem' }}>
                {error}
              </p>
            )}
            {abierto && (
              <div className="stack" style={{ gap: '0.35rem' }}>
                <span className="tiny muted">
                  Origen: {SOURCE_LABELS[movimiento.source] ?? movimiento.source} · Método:{' '}
                  {movimiento.payment_method}
                </span>
                <span className="tiny mono" style={{ whiteSpace: 'pre-wrap', opacity: 0.85 }}>
                  {movimiento.raw_text || 'Sin texto original guardado.'}
                </span>
              </div>
            )}
          </td>
        </tr>
      )}
    </>
  );
}

function EstadoCelda({
  estado,
  deshabilitado,
  onCambio
}: {
  estado: ConfirmationState;
  deshabilitado: boolean;
  onCambio: (estado: ConfirmationState) => void;
}) {
  // Para lo pendiente se muestran dos botones directos (confirmar / rechazar):
  // es la accion que el usuario repite decenas de veces y un desplegable la
  // convierte en tres clics. Para lo ya resuelto basta la etiqueta.
  if (estado === 'PENDING') {
    return (
      <div className="row" style={{ gap: '0.35rem' }}>
        <button className="btn btn--sm" disabled={deshabilitado} onClick={() => onCambio('CONFIRMED')}>
          Confirmar
        </button>
        <button
          className="btn btn--ghost btn--sm"
          disabled={deshabilitado}
          onClick={() => onCambio('REJECTED')}
        >
          Rechazar
        </button>
      </div>
    );
  }

  const clase =
    estado === 'REJECTED' ? 'tag tag--gasto' : estado === 'AUTO_CONFIRMED' ? 'tag tag--info' : 'tag tag--ingreso';

  return (
    <div className="row" style={{ gap: '0.4rem' }}>
      <span className={clase}>{STATE_LABELS[estado]}</span>
      <button
        className="btn btn--ghost btn--sm"
        disabled={deshabilitado}
        onClick={() => onCambio('PENDING')}
        title="Volver a dejarlo por confirmar"
      >
        Deshacer
      </button>
    </div>
  );
}
