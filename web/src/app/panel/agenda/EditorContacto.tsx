'use client';

import { useState, useTransition } from 'react';
import { guardarContacto } from '../actions';
import type { AgendaEntry, Category } from '@/lib/types';

export default function EditorContacto({
  categorias,
  existente,
  compacto = false
}: {
  categorias: Category[];
  existente?: AgendaEntry;
  compacto?: boolean;
}) {
  const [abierto, setAbierto] = useState(false);
  const [identificador, setIdentificador] = useState(existente?.account_identifier ?? '');
  const [nombre, setNombre] = useState(existente?.display_name ?? '');
  const [categoria, setCategoria] = useState(existente?.default_category_id ?? '');
  const [error, setError] = useState<string | null>(null);
  const [pendiente, startTransition] = useTransition();

  if (!abierto) {
    return (
      <button className={`btn ${compacto ? 'btn--ghost btn--sm' : ''}`} onClick={() => setAbierto(true)}>
        {existente ? 'Editar' : 'Nuevo contacto'}
      </button>
    );
  }

  return (
    <form
      className="card stack"
      style={{ width: '100%', minWidth: 260 }}
      onSubmit={(e) => {
        e.preventDefault();
        setError(null);
        startTransition(async () => {
          const r = await guardarContacto({
            id: existente?.id,
            account_identifier: identificador,
            display_name: nombre,
            default_category_id: categoria || null
          });
          if (r.ok) setAbierto(false);
          else setError(r.error);
        });
      }}
    >
      <div className="field">
        <label>Número o identificador</label>
        <input
          className="input"
          required
          placeholder="3001234567, *1234, NOMBRE COMERCIO…"
          value={identificador}
          onChange={(e) => setIdentificador(e.target.value)}
        />
        <span className="tiny muted">Tal como aparece en la notificación del banco.</span>
      </div>

      <div className="field">
        <label>Nombre a mostrar</label>
        <input className="input" required value={nombre} onChange={(e) => setNombre(e.target.value)} />
      </div>

      <div className="field">
        <label>Categoría por defecto</label>
        <select className="input" value={categoria} onChange={(e) => setCategoria(e.target.value)}>
          <option value="">Ninguna</option>
          {categorias.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name} ({c.type === 'INCOME' ? 'ingreso' : 'gasto'})
            </option>
          ))}
        </select>
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
