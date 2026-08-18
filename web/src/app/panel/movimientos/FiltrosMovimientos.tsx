'use client';

import { useRouter, useSearchParams } from 'next/navigation';
import { useState } from 'react';
import { BANK_LABELS, STATE_LABELS, type Category } from '@/lib/types';

/**
 * Los filtros viven en la URL, no en estado de React: asi un filtro concreto
 * ("gastos sin clasificar de Bancolombia") se puede guardar en marcadores o
 * compartir, y el boton Atras del navegador hace lo esperado.
 */
export default function FiltrosMovimientos({
  categorias,
  bancos,
  valores
}: {
  categorias: Category[];
  bancos: string[];
  valores: { estado: string; banco: string; categoria: string; q: string };
}) {
  const router = useRouter();
  const params = useSearchParams();
  const [texto, setTexto] = useState(valores.q);

  function aplicar(clave: string, valor: string) {
    const qs = new URLSearchParams(params.toString());
    if (valor) qs.set(clave, valor);
    else qs.delete(clave);
    qs.delete('pagina'); // cambiar un filtro y quedarse en la pagina 7 no tiene sentido
    router.push(`/panel/movimientos?${qs}`);
  }

  const hayFiltros = valores.estado || valores.banco || valores.categoria || valores.q;

  return (
    <div className="card card--flat row wrap" style={{ gap: '0.9rem', alignItems: 'flex-end' }}>
      <div className="field" style={{ flex: '1 1 200px' }}>
        <label htmlFor="q">Buscar contraparte</label>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            aplicar('q', texto.trim());
          }}
        >
          <input
            id="q"
            className="input"
            placeholder="Nombre, comercio, número…"
            value={texto}
            onChange={(e) => setTexto(e.target.value)}
          />
        </form>
      </div>

      <div className="field" style={{ flex: '0 1 180px' }}>
        <label htmlFor="estado">Estado</label>
        <select
          id="estado"
          className="input"
          value={valores.estado}
          onChange={(e) => aplicar('estado', e.target.value)}
        >
          <option value="">Todos</option>
          {Object.entries(STATE_LABELS).map(([k, v]) => (
            <option key={k} value={k}>
              {v}
            </option>
          ))}
        </select>
      </div>

      <div className="field" style={{ flex: '0 1 170px' }}>
        <label htmlFor="banco">Banco</label>
        <select
          id="banco"
          className="input"
          value={valores.banco}
          onChange={(e) => aplicar('banco', e.target.value)}
        >
          <option value="">Todos</option>
          {bancos.map((b) => (
            <option key={b} value={b}>
              {BANK_LABELS[b] ?? b}
            </option>
          ))}
        </select>
      </div>

      <div className="field" style={{ flex: '0 1 200px' }}>
        <label htmlFor="categoria">Categoría</label>
        <select
          id="categoria"
          className="input"
          value={valores.categoria}
          onChange={(e) => aplicar('categoria', e.target.value)}
        >
          <option value="">Todas</option>
          <option value="sin">Sin clasificar</option>
          {categorias.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
      </div>

      {hayFiltros && (
        <button className="btn btn--ghost btn--sm" onClick={() => router.push('/panel/movimientos')}>
          Limpiar
        </button>
      )}
    </div>
  );
}
