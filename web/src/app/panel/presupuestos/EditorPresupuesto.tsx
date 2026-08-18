'use client';

import { useState, useTransition } from 'react';
import { guardarPresupuesto } from '../actions';
import type { Budget, Category } from '@/lib/types';

export default function EditorPresupuesto({
  categorias,
  mes,
  anio,
  existente,
  compacto = false
}: {
  categorias: Category[];
  mes: number;
  anio: number;
  existente?: Budget;
  compacto?: boolean;
}) {
  const [abierto, setAbierto] = useState(false);
  const [categoria, setCategoria] = useState(existente?.category_id ?? categorias[0]?.id ?? '');
  // La UI trabaja en pesos; la conversion a centavos ocurre en la Server Action.
  const [limite, setLimite] = useState(existente ? String(existente.monthly_limit / 100) : '');
  const [error, setError] = useState<string | null>(null);
  const [pendiente, startTransition] = useTransition();

  if (!abierto) {
    return (
      <button className={`btn ${compacto ? 'btn--ghost btn--sm' : ''}`} onClick={() => setAbierto(true)}>
        {existente ? 'Editar' : 'Nuevo presupuesto'}
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
          const r = await guardarPresupuesto({
            id: existente?.id,
            category_id: categoria,
            monthly_limit_pesos: Number(limite),
            month: mes,
            year: anio
          });
          if (r.ok) setAbierto(false);
          else setError(r.error);
        });
      }}
    >
      <div className="field">
        <label htmlFor={`cat-${existente?.id ?? 'nuevo'}`}>Categoría</label>
        <select
          id={`cat-${existente?.id ?? 'nuevo'}`}
          className="input"
          value={categoria}
          onChange={(e) => setCategoria(e.target.value)}
          // Cambiar la categoria de un presupuesto existente chocaria con el
          // indice unico (usuario + categoria + mes + año); se crea otro.
          disabled={Boolean(existente)}
        >
          {categorias.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
      </div>

      <div className="field">
        <label htmlFor={`lim-${existente?.id ?? 'nuevo'}`}>Límite mensual (pesos)</label>
        <input
          id={`lim-${existente?.id ?? 'nuevo'}`}
          className="input"
          type="number"
          min="1"
          step="1000"
          required
          value={limite}
          onChange={(e) => setLimite(e.target.value)}
        />
      </div>

      {error && (
        <p className="small" role="alert" style={{ color: 'var(--barro)', margin: 0 }}>
          {error}
        </p>
      )}

      <div className="row" style={{ gap: '0.5rem' }}>
        <button className="btn" type="submit" disabled={pendiente || !categoria}>
          {pendiente ? 'Guardando…' : 'Guardar'}
        </button>
        <button className="btn btn--ghost" type="button" onClick={() => setAbierto(false)}>
          Cancelar
        </button>
      </div>
    </form>
  );
}
