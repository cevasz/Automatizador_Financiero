'use client';

import { useState, useTransition } from 'react';
import { useRouter } from 'next/navigation';
import { borrarTodosMisDatos } from '../actions';

const FRASE = 'BORRAR';

export default function BorrarTodo() {
  const router = useRouter();
  const [confirmacion, setConfirmacion] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [listo, setListo] = useState(false);
  const [pendiente, startTransition] = useTransition();

  // Escribir la palabra, y no solo un confirm(): es irreversible y un confirm()
  // se acepta por reflejo.
  return (
    <div className="stack" style={{ gap: '0.6rem' }}>
      <div className="field" style={{ maxWidth: 320 }}>
        <label htmlFor="confirmar">
          Escribe <code>{FRASE}</code> para habilitar el botón
        </label>
        <input
          id="confirmar"
          className="input"
          value={confirmacion}
          onChange={(e) => setConfirmacion(e.target.value)}
        />
      </div>

      <button
        className="btn btn--danger"
        style={{ alignSelf: 'flex-start' }}
        disabled={confirmacion !== FRASE || pendiente}
        onClick={() => {
          setError(null);
          startTransition(async () => {
            const r = await borrarTodosMisDatos();
            if (r.ok) {
              setListo(true);
              setConfirmacion('');
              router.refresh();
            } else {
              setError(r.error);
            }
          });
        }}
      >
        {pendiente ? 'Borrando…' : 'Borrar todo de la nube'}
      </button>

      {listo && (
        <p className="small" role="status" style={{ color: 'var(--oliva)', margin: 0 }}>
          Listo. No queda nada tuyo en el servidor.
        </p>
      )}
      {error && (
        <p className="small" role="alert" style={{ color: 'var(--barro)', margin: 0 }}>
          {error}
        </p>
      )}
    </div>
  );
}
