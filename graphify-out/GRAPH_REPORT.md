# Graph Report - .  (2026-07-14)

## Corpus Check
- Corpus is ~3,367 words - fits in a single context window. You may not need a graph.

## Summary
- 46 nodes · 44 edges · 9 communities (7 shown, 2 thin omitted)
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Community 0
- Community 1
- Community 2
- Community 3
- Community 4
- Community 5
- Community 6
- Community 7
- Community 8

## God Nodes (most connected - your core abstractions)
1. `6. Módulos funcionales` - 11 edges
2. `Contexto del proyecto — App de Contabilidad Financiera Automática (Colombia)` - 7 edges
3. `2. Problema y contexto` - 4 edges
4. `5. Arquitectura general del sistema` - 4 edges
5. `8. Seguridad y privacidad` - 3 edges
6. `9. Modelo de sostenibilidad y financiación` - 3 edges
7. `3. Visión del producto y propuesta de valor` - 2 edges
8. `6.2 Agenda financiera` - 2 edges
9. `Qué es` - 1 edges
10. `Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente)` - 1 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities (9 total, 2 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.20
Nodes (9): 10. Stack tecnológico propuesto, 11. Roadmap por fases, 12. Riesgos y mitigaciones, 13. Métricas de éxito del MVP, 14. Próximos pasos, 1. Resumen ejecutivo, 4. Decisiones de alcance del MVP, 7. Modelo de datos (entidades principales) (+1 more)

### Community 1 - "Community 1"
Cohesion: 0.20
Nodes (10): 6.10 Confirmación ligera de movimientos, 6.1 Captura automática de movimientos, 6.3 Clasificación automática, 6.4 Dashboard y cronología, 6.5 Metas de ahorro, 6.6 Presupuestos por categoría, 6.7 Alertas y detección de patrones, 6.8 Escaneo de comprobantes (OCR) (+2 more)

### Community 2 - "Community 2"
Cohesion: 0.25
Nodes (7): Arquitectura interna (dentro de la app Android), Contexto del proyecto — App de Contabilidad Financiera Automática (Colombia), Convenciones de código, Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente), Entidades bancarias soportadas en el MVP, Qué es, Qué NO hacer

### Community 3 - "Community 3"
Cohesion: 0.50
Nodes (4): 2.1 El problema, 2.2 Contexto regulatorio (Colombia, 2026), 2.3 Restricciones técnicas de plataforma, 2. Problema y contexto

### Community 4 - "Community 4"
Cohesion: 0.50
Nodes (4): 5.1 Componentes principales, 5.2 Principio de captura multi-fuente, 5.3 Flujo de un movimiento, 5. Arquitectura general del sistema

### Community 5 - "Community 5"
Cohesion: 0.67
Nodes (3): 8.1 Principios de privacidad por diseño, 8.2 Cumplimiento normativo, 8. Seguridad y privacidad

### Community 6 - "Community 6"
Cohesion: 0.67
Nodes (3): 9.1 Aportes voluntarios con recompensa, 9.2 Capas premium no esenciales (fase posterior al MVP), 9. Modelo de sostenibilidad y financiación

## Knowledge Gaps
- **36 isolated node(s):** `Qué es`, `Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente)`, `Arquitectura interna (dentro de la app Android)`, `Convenciones de código`, `Entidades bancarias soportadas en el MVP` (+31 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `6. Módulos funcionales` connect `Community 1` to `Community 0`, `Community 8`?**
  _High betweenness centrality (0.343) - this node is a cross-community bridge._
- **Why does `2. Problema y contexto` connect `Community 3` to `Community 0`?**
  _High betweenness centrality (0.106) - this node is a cross-community bridge._
- **Why does `5. Arquitectura general del sistema` connect `Community 4` to `Community 0`?**
  _High betweenness centrality (0.106) - this node is a cross-community bridge._
- **What connects `Qué es`, `Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente)`, `Arquitectura interna (dentro de la app Android)` to the rest of the system?**
  _36 weakly-connected nodes found - possible documentation gaps or missing edges._