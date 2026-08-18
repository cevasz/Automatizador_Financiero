'use client';

import { useState, useTransition } from 'react';
import { abonarMeta } from '../actions';

export default function AbonarMeta({ id }: { id: string }) {
  const [abierto, setAbierto] = useState(false);
  const [monto, setMonto] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [pendiente, startTransition] = useTransition();

  if (!abierto) {
    return (
      <button className="btn btn--sm" onClick={() => setAbierto(true)}>
        Abonar
      </button>
    );
  }

  return (
    <form
      className="row wrap"
      style={{ gap: '0.4rem', width: '100%' }}
      onSubmit={(e) => {
        e.preventDefault();
        setError(null);
        startTransition(async () => {
          const r = await abonarMeta(id, Number(monto));
          if (r.ok) {
            setAbierto(false);
            setMonto('');
          } else {
            setError(r.error);
          }
        });
      }}
    >
      <input
        className="input"
        style={{ flex: '1 1 120px' }}
        type="number"
        min="1"
        step="1000"
        required
        autoFocus
        placeholder="Monto en pesos"
        value={monto}
        onChange={(e) => setMonto(e.target.value)}
      />
      <button className="btn btn--sm" type="submit" disabled={pendiente}>
        {pendiente ? '…' : 'Sumar'}
      </button>
      <button className="btn btn--ghost btn--sm" type="button" onClick={() => setAbierto(false)}>
        Cancelar
      </button>
      {error && (
        <span className="tiny" role="alert" style={{ color: 'var(--barro)', width: '100%' }}>
          {error}
        </span>
      )}
    </form>
  );
}
