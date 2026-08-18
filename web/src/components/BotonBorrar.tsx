'use client';

import { useState, useTransition } from 'react';

/**
 * Boton de borrado con confirmacion. La accion llega ya "atada" (bind) al id
 * desde el Server Component, para no tener que exponer una accion generica que
 * reciba cualquier id.
 */
export default function BotonBorrar({
  accion,
  confirmacion,
  etiqueta = 'Eliminar'
}: {
  accion: () => Promise<{ ok: boolean; error?: string }>;
  confirmacion: string;
  etiqueta?: string;
}) {
  const [pendiente, startTransition] = useTransition();
  const [error, setError] = useState<string | null>(null);

  return (
    <span className="stack" style={{ gap: '0.25rem' }}>
      <button
        className="btn btn--danger btn--sm"
        disabled={pendiente}
        onClick={() => {
          if (!confirm(confirmacion)) return;
          setError(null);
          startTransition(async () => {
            const r = await accion();
            if (!r.ok) setError(r.error ?? 'No se pudo eliminar.');
          });
        }}
      >
        {pendiente ? 'Eliminando…' : etiqueta}
      </button>
      {error && (
        <span className="tiny" role="alert" style={{ color: 'var(--barro)' }}>
          {error}
        </span>
      )}
    </span>
  );
}
