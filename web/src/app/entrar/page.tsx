import { Suspense } from 'react';
import LoginForm from './LoginForm';

export const metadata = { title: 'Entrar — Kivo' };

export default function EntrarPage() {
  return (
    <main
      style={{
        minHeight: '100dvh',
        display: 'grid',
        placeItems: 'center',
        padding: '2rem 1rem'
      }}
    >
      <div style={{ width: '100%', maxWidth: 420 }} className="stack">
        <header className="stack" style={{ gap: '0.35rem', textAlign: 'center' }}>
          <p className="eyebrow">Kivo</p>
          <h1>Tu historial financiero, en cualquier pantalla</h1>
          <p className="muted small">
            La captura de movimientos sigue ocurriendo en tu teléfono. Este panel es
            una copia sincronizada para revisar y corregir con calma.
          </p>
        </header>

        <Suspense fallback={<div className="card muted small">Cargando…</div>}>
          <LoginForm />
        </Suspense>

        <p className="tiny muted" style={{ textAlign: 'center' }}>
          Kivo nunca pide usuario ni clave de tu banco. Esta cuenta es solo de Kivo.
        </p>
      </div>
    </main>
  );
}
