'use client';

import { useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { createClient } from '@/lib/supabase/client';

type Modo = 'entrar' | 'crear';

export default function LoginForm() {
  const router = useRouter();
  const params = useSearchParams();
  const destino = params.get('desde') ?? '/panel';

  const [modo, setModo] = useState<Modo>('entrar');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [aviso, setAviso] = useState<string | null>(null);

  async function enviar(e: React.FormEvent) {
    e.preventDefault();
    setCargando(true);
    setError(null);
    setAviso(null);

    const supabase = createClient();

    try {
      if (modo === 'crear') {
        const { data, error } = await supabase.auth.signUp({ email, password });
        if (error) throw error;
        // Si el proyecto tiene confirmacion por correo activada, signUp devuelve
        // usuario pero sin sesion: mandar al panel aqui daria un rebote inmediato
        // de vuelta a /entrar sin explicar nada.
        if (!data.session) {
          setAviso('Revisa tu correo para confirmar la cuenta y vuelve a entrar.');
          return;
        }
      } else {
        const { error } = await supabase.auth.signInWithPassword({ email, password });
        if (error) throw error;
      }

      // refresh() antes de push(): el layout del panel se renderiza en el
      // servidor y necesita ver la cookie de sesion recien escrita.
      router.refresh();
      router.push(destino);
    } catch (e) {
      setError(traducirError(e));
    } finally {
      setCargando(false);
    }
  }

  return (
    <form className="card stack" onSubmit={enviar}>
      <div className="row" role="tablist" style={{ gap: '0.5rem' }}>
        <button
          type="button"
          role="tab"
          aria-selected={modo === 'entrar'}
          className={`btn btn--sm ${modo === 'entrar' ? '' : 'btn--ghost'}`}
          onClick={() => setModo('entrar')}
        >
          Entrar
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={modo === 'crear'}
          className={`btn btn--sm ${modo === 'crear' ? '' : 'btn--ghost'}`}
          onClick={() => setModo('crear')}
        >
          Crear cuenta
        </button>
      </div>

      <div className="field">
        <label htmlFor="email">Correo</label>
        <input
          id="email"
          className="input"
          type="email"
          autoComplete="email"
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
      </div>

      <div className="field">
        <label htmlFor="password">Contraseña</label>
        <input
          id="password"
          className="input"
          type="password"
          autoComplete={modo === 'crear' ? 'new-password' : 'current-password'}
          required
          minLength={8}
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        {modo === 'crear' && <span className="tiny muted">Mínimo 8 caracteres.</span>}
      </div>

      {error && (
        <p className="small" role="alert" style={{ color: 'var(--barro)', margin: 0 }}>
          {error}
        </p>
      )}
      {aviso && (
        <p className="small" role="status" style={{ color: 'var(--oliva)', margin: 0 }}>
          {aviso}
        </p>
      )}

      <button className="btn" type="submit" disabled={cargando}>
        {cargando ? 'Un momento…' : modo === 'crear' ? 'Crear cuenta' : 'Entrar'}
      </button>
    </form>
  );
}

// Supabase responde en inglés y con mensajes técnicos; mostrarlos tal cual deja
// al usuario sin saber qué hacer.
function traducirError(e: unknown): string {
  const msg = e instanceof Error ? e.message : String(e);
  if (/Invalid login credentials/i.test(msg)) return 'Correo o contraseña incorrectos.';
  if (/User already registered/i.test(msg)) return 'Ese correo ya tiene una cuenta. Entra en vez de crearla.';
  if (/Password should be/i.test(msg)) return 'La contraseña es demasiado corta (mínimo 8 caracteres).';
  if (/Email not confirmed/i.test(msg)) return 'Falta confirmar el correo. Revisa tu bandeja de entrada.';
  if (/Falta NEXT_PUBLIC_SUPABASE/i.test(msg)) return msg;
  return `No se pudo completar: ${msg}`;
}
