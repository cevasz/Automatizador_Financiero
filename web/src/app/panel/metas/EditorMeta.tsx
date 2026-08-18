'use client';

import { useState, useTransition } from 'react';
import { guardarMeta } from '../actions';
import type { SavingsGoal } from '@/lib/types';

export default function EditorMeta({
  existente,
  compacto = false
}: {
  existente?: SavingsGoal;
  compacto?: boolean;
}) {
  const [abierto, setAbierto] = useState(false);
  const [nombre, setNombre] = useState(existente?.name ?? '');
  const [objetivo, setObjetivo] = useState(existente ? String(existente.target_amount / 100) : '');
  const [fecha, setFecha] = useState(
    (existente?.target_date ?? new Date(Date.now() + 90 * 86_400_000).toISOString()).slice(0, 10)
  );
  const [error, setError] = useState<string | null>(null);
  const [pendiente, startTransition] = useTransition();

  if (!abierto) {
    return (
      <button className={`btn ${compacto ? 'btn--ghost btn--sm' : ''}`} onClick={() => setAbierto(true)}>
        {existente ? 'Editar' : 'Nueva meta'}
      </button>
    );
  }

  return (
    <form
      className="card stack"
      style={{ width: '100%' }}
      onSubmit={(e) => {
        e.preventDefault();
        setError(null);
        startTransition(async () => {
          const r = await guardarMeta({
            id: existente?.id,
            name: nombre,
            target_pesos: Number(objetivo),
            target_date: fecha
          });
          if (r.ok) setAbierto(false);
          else setError(r.error);
        });
      }}
    >
      <div className="field">
        <label>Nombre</label>
        <input className="input" required value={nombre} onChange={(e) => setNombre(e.target.value)} />
      </div>
      <div className="field">
        <label>Monto objetivo (pesos)</label>
        <input
          className="input"
          type="number"
          min="1"
          step="1000"
          required
          value={objetivo}
          onChange={(e) => setObjetivo(e.target.value)}
        />
      </div>
      <div className="field">
        <label>Fecha objetivo</label>
        <input className="input" type="date" required value={fecha} onChange={(e) => setFecha(e.target.value)} />
      </div>

      {error && (
        <p className="small" role="alert" style={{ color: 'var(--barro)', margin: 0 }}>
          {error}
        </p>
      )}

      <div className="row" style={{ gap: '0.5rem' }}>
        <button className="btn" type="submit" disabled={pendiente}>
          {pendiente ? 'Guardando…' : 'Guardar'}
        </button>
        <button className="btn btn--ghost" type="button" onClick={() => setAbierto(false)}>
          Cancelar
        </button>
      </div>
    </form>
  );
}
