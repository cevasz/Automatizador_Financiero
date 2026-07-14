<!-- converted from SDD_App_Finanzas.docx -->


Documento de Diseño de Software (SDD)
Aplicación de Contabilidad Financiera Personal Automática
Ecosistema móvil + backend + panel web para Colombia
Versión 1.0 — Julio de 2026

# Tabla de contenido


# 1. Resumen ejecutivo
La mayoría de las transacciones financieras en Colombia ya ocurren de forma digital (Nequi, Bancolombia, Daviplata, Nu, Lulo Bank, transferencias, QR, PSE), pero esa información queda fragmentada entre aplicaciones bancarias que no se comunican entre sí. El usuario promedio no tiene una vista unificada de sus finanzas ni una clasificación automática de sus gastos.
Este proyecto propone una aplicación móvil (Android, con escalabilidad planificada a iOS) conectada a un backend y un panel web, capaz de construir automáticamente el historial financiero del usuario a partir de sus notificaciones bancarias y otras fuentes de captura, sin exigirle registro manual de cada movimiento.
El diferencial central frente a apps de finanzas genéricas es doble: (1) una 'agenda financiera' que traduce números de teléfono (Nequi) en nombres de comercio reales, resolviendo un problema muy específico del ecosistema de pagos colombiano; y (2) una arquitectura de captura de datos multi-fuente diseñada para migrar, a mediano plazo, hacia el nuevo Sistema de Finanzas Abiertas obligatorio en Colombia (Decreto 0368 de 2026).
El producto se plantea como gratuito en su núcleo — registrar, ver y clasificar movimientos nunca tendrá costo — financiado mediante aportes voluntarios con recompensas simbólicas y de comunidad, y capas premium opcionales orientadas a independientes y pequeños negocios.
# 2. Problema y contexto
## 2.1 El problema
Hoy en día una persona puede recibir y enviar dinero por seis o siete canales distintos en la misma semana. Cada aplicación mantiene su propio historial aislado, ninguna clasifica automáticamente los gastos, y cuando llega una notificación como “Transferencia enviada a 3204567890” resulta prácticamente imposible recordar, semanas después, a quién correspondía ese número. Esto obliga a revisar chats, buscar comprobantes o simplemente perder el registro del gasto.
## 2.2 Contexto regulatorio (Colombia, 2026)
En abril de 2026 el Gobierno colombiano expidió el Decreto 0368 de 2026, que hace obligatorio el Sistema de Finanzas Abiertas (Open Finance) para todas las entidades vigiladas por la Superintendencia Financiera de Colombia (bancos, SEDPE como Nequi y Daviplata, compañías de financiamiento, fiduciarias, entre otras). Bajo este marco, el dato financiero es del usuario, y este puede autorizar que circule hacia terceros bajo condiciones estrictas de consentimiento.
La implementación es gradual: la Superintendencia Financiera tiene hasta octubre de 2026 para publicar el cronograma de estándares técnicos y hasta doce meses para poner en marcha el directorio de participantes. Esto significa que, en el corto plazo, la captura de datos seguirá dependiendo de métodos como la lectura de notificaciones, pero el diseño del sistema debe anticipar la migración hacia APIs oficiales de Open Finance en un horizonte de 12 a 24 meses.
Adicionalmente, al tratarse de datos financieros personales, el proyecto está sujeto a la Ley 1581 de 2012 (régimen general de protección de datos personales / habeas data), lo que exige registro como responsable del tratamiento, consentimiento explícito y diferenciado por fuente de datos, y mecanismos reales de consulta, corrección y eliminación de la información.
## 2.3 Restricciones técnicas de plataforma
Android permite, con autorización explícita del usuario, leer notificaciones de otras aplicaciones mediante NotificationListenerService. Sin embargo, Google Play clasifica este permiso —junto con READ_SMS y ACCESSIBILITY— como de alto riesgo por su historial de abuso en fraude financiero, lo que implica declaraciones obligatorias en Play Console y mayor escrutinio en cada actualización. El uso de la API de Accesibilidad para ejecutar acciones de forma autónoma está explícitamente prohibido por la política de Google.
iOS no permite a aplicaciones de terceros leer las notificaciones de otras apps. Esta es una restricción estructural de la plataforma, no un problema de diseño, por lo que la captura en iOS requiere mecanismos alternativos (ver sección 6.2).
# 3. Visión del producto y propuesta de valor
Visión: Ser la capa de contabilidad automática que todo colombiano necesita pero que ningún banco individual le ofrece, construida sobre el principio de que el dato financiero pertenece al usuario.
## 3.1 Propuesta de valor diferencial
- Historial financiero unificado sin registro manual, construido a partir de la actividad diaria real.
- Agenda financiera: traduce números de teléfono y cuentas en nombres de comercios reales, resolviendo el problema específico de Nequi/Bancolombia en Colombia.
- Arquitectura multi-fuente y a prueba de futuro: notificaciones hoy, Open Finance mañana, sin rediseñar el producto.
- Núcleo 100% gratuito: nunca se cobra por ver o clasificar el propio historial financiero.
- Privacidad por diseño: la app nunca solicita credenciales bancarias ni accede directamente a cuentas.
- Componente comunitario: el aprendizaje de la agenda financiera mejora con cada usuario nuevo (efecto de red).
# 4. Decisiones de alcance del MVP
Las siguientes decisiones fueron confirmadas para la primera versión del producto y determinan la arquitectura descrita en este documento:

# 5. Arquitectura general del sistema
## 5.1 Componentes principales
- Aplicación móvil (Android): captura eventos financieros, clasifica localmente, gestiona la agenda financiera y funciona offline-first.
- Backend / API: procesa, aplica reglas de negocio, sincroniza y expone datos al panel web. Punto único de verdad cuando hay múltiples dispositivos.
- Panel web: análisis profundo, exportación, gestión de presupuestos y metas, configuración de cuenta.
## 5.2 Principio de captura multi-fuente
Para no depender de un único mecanismo frágil, la app se diseña con una capa de abstracción de “fuentes de datos”, de modo que agregar o retirar una fuente no afecte el resto del sistema:
- Fuente 1 — NotificationListenerService (Android): captura en tiempo real, mecanismo principal del MVP.
- Fuente 2 — Share Extension / “compartir hacia la app”: el usuario comparte manualmente una notificación o captura de pantalla; mecanismo de respaldo en Android y única vía viable en iOS.
- Fuente 3 — OCR sobre fotos de comprobantes o capturas de pantalla, reutilizando el mismo motor del escaneo de facturas.
- Fuente 4 — Parseo de correos de confirmación bancaria (opcional, con consentimiento explícito vía API de correo).
- Fuente 5 — Integración oficial vía Open Finance, planificada como fase posterior una vez la Superintendencia Financiera publique los estándares técnicos (estimado a partir de finales de 2026).
Cada fuente produce un mismo objeto interno estandarizado (“Movimiento crudo”) que entra al motor de reglas, de modo que agregar Open Finance en el futuro no requiere rediseñar la clasificación ni la agenda financiera.
## 5.3 Flujo de un movimiento
- 1. Captura: la notificación llega y el listener extrae texto crudo.
- 2. Parseo: expresiones regulares por entidad (Bancolombia, Nequi, Daviplata, Nu, Lulo) extraen tipo, valor, medio, contraparte y fecha.
- 3. Enriquecimiento: se cruza la contraparte (número o cuenta) contra la agenda financiera del usuario.
- 4. Clasificación: reglas asignan categoría según comercio conocido, palabras clave o histórico del mismo número.
- 5. Confirmación ligera: en movimientos nuevos o de baja confianza, se pide confirmación rápida al usuario (patrón tipo swipe).
- 6. Persistencia y sincronización: se guarda localmente y se sincroniza con el backend si hay sesión en la nube activa.
# 6. Módulos funcionales
## 6.1 Captura automática de movimientos
Lee notificaciones autorizadas de apps bancarias y extrae tipo de movimiento, valor, medio de pago, contraparte y fecha, sin solicitar nunca credenciales ni acceso directo a cuentas.
## 6.2 Agenda financiera
Permite registrar manualmente un número o cuenta asociado a un comercio o persona (nombre, categoría, color). En futuras notificaciones hacia ese mismo número, la app completa automáticamente el comercio y la categoría. Cuando un número desconocido se repite de forma consistente en una misma categoría, la app sugiere proactivamente crear la entrada en la agenda.
### Refuerzo: aprendizaje comunitario opt-in
Cuando un usuario confirma que un número corresponde a un comercio, esa asociación puede sugerirse —de forma anonimizada y solo con consentimiento explícito— a otros usuarios que reciban movimientos hacia el mismo número. Esto convierte la agenda financiera en un activo que mejora con cada usuario nuevo, en lugar de un catálogo que cada persona debe construir desde cero.
## 6.3 Clasificación automática
Categorías base de ingreso (salario, freelance, devolución, préstamo, venta) y de egreso (mercado, transporte, restaurantes, gasolina, servicios, salud, educación, entretenimiento), extensibles por el usuario.
## 6.4 Dashboard y cronología
Vista de gasto del día, la semana y el mes; balance de ingresos, egresos y ahorro; gráfico de gastos por categoría; línea de tiempo cronológica de movimientos.
## 6.5 Metas de ahorro
El usuario define una meta y un plazo; la app calcula el aporte mensual necesario y muestra el avance real frente al plan.
## 6.6 Presupuestos por categoría
Límites mensuales configurables por categoría con barra de progreso y alerta al acercarse o superar el límite.
## 6.7 Alertas y detección de patrones
Detección de gasto inusualmente alto frente al promedio histórico, rachas de gasto en una misma categoría (por ejemplo comida rápida varios días seguidos), y reconocimiento de movimientos recurrentes (suscripciones, arriendos) mediante reglas de periodicidad simples, sin necesidad de modelos de aprendizaje automático en el MVP.
## 6.8 Escaneo de comprobantes (OCR)
Captura de foto de factura o comprobante físico/digital y extracción automática de comercio, valor y fecha, reutilizando el mismo pipeline de clasificación que las notificaciones.
## 6.9 Exportación
Exportación a CSV, Excel y PDF para uso contable o declaración de renta, disponible en el panel web.
## 6.10 Confirmación ligera de movimientos
Mecanismo tipo swipe para confirmar o corregir movimientos de baja confianza, reduciendo el error de clasificación sin exigir registro manual completo.
# 7. Modelo de datos (entidades principales)

# 8. Seguridad y privacidad
## 8.1 Principios de privacidad por diseño
- La app nunca solicita usuario/clave ni credenciales bancarias.
- Solo se procesan las notificaciones que el usuario autoriza explícitamente, app por app.
- Cifrado en reposo (dispositivo y servidor) y en tránsito (TLS) para todos los datos financieros.
- Acceso protegido por biometría (huella o reconocimiento facial) además de la autenticación de cuenta.
- El usuario puede exportar o eliminar toda su información en cualquier momento (derecho de habeas data).
- Ninguna asociación de la agenda financiera se comparte hacia la comunidad sin consentimiento explícito y sin anonimización previa.
## 8.2 Cumplimiento normativo
- Registro como responsable del tratamiento de datos personales bajo la Ley 1581 de 2012.
- Política de tratamiento de datos y consentimientos diferenciados por fuente de captura (notificaciones, OCR, correo, Open Finance).
- Declaración de funciones financieras en Google Play Console y justificación explícita del permiso de notificaciones.
- Diseño preparado para operar como tercero dentro del Sistema de Finanzas Abiertas cuando la Superintendencia Financiera habilite la participación voluntaria de terceros no vigilados.
# 9. Modelo de sostenibilidad y financiación
El núcleo del producto —registrar, ver y clasificar movimientos— es y será siempre gratuito e ilimitado. La sostenibilidad se busca sin condicionar el acceso a la contabilidad personal.
## 9.1 Aportes voluntarios con recompensa
El usuario puede aportar una cantidad libre y periódica, similar a un modelo de apoyo tipo comunidad (no una compra de funciones). A cambio recibe reconocimientos simbólicos y de pertenencia, nunca funciones esenciales bloqueadas:

## 9.2 Capas premium no esenciales (fase posterior al MVP)
- Exportación contable avanzada y respaldo multi-dispositivo en la nube más allá del historial reciente.
- Versión “Pro” orientada a independientes y pequeños negocios, con separación de finanzas personales/negocio y reportes para declaración de renta.
- Licenciamiento white-label a cooperativas de ahorro, fondos de empleados o programas universitarios de educación financiera.
- Insights agregados y anonimizados sobre tendencias de gasto, ofrecidos únicamente con consentimiento opt-in explícito y sin datos identificables.
# 10. Stack tecnológico propuesto

# 11. Roadmap por fases

# 12. Riesgos y mitigaciones

# 13. Métricas de éxito del MVP
- Porcentaje de movimientos clasificados correctamente sin intervención manual del usuario (meta inicial orientativa: >70%).
- Número de entradas creadas en la agenda financiera por usuario activo, como indicador de que el producto resuelve el problema central.
- Retención a 30 y 90 días.
- Porcentaje de usuarios que realizan al menos un aporte voluntario, y monto promedio de aporte.
- Tasa de error/corrección en la confirmación ligera de movimientos (para calibrar la confianza del motor de reglas).
# 14. Próximos pasos
- Definir y documentar el conjunto inicial de expresiones regulares por entidad bancaria (Bancolombia, Nequi, Daviplata, Nu, Lulo Bank).
- Construir el prototipo de NotificationListenerService y validar la captura real en dispositivo antes de invertir en el resto de módulos.
- Redactar la política de tratamiento de datos y el flujo de consentimiento diferenciado por fuente.
- Diseñar el algoritmo de generación de la “huella financiera” como primer entregable visual del sistema de aportes.
- Definir la paleta y el sistema de diseño (terracota → ocre) como identidad visual base de la app y del tema “fundador”.
| Decisión | Detalle |
| --- | --- |
| Plataforma | Android nativo primero. Arquitectura preparada desde el día 1 para escalar a iOS (backend y modelo de datos agnósticos de plataforma). |
| Motor de clasificación | Reglas simples + expresiones regulares sobre el texto de la notificación. Sin dependencia de LLM ni costo variable de API en el MVP. |
| Modelo de negocio | Núcleo gratuito e ilimitado. Sostenibilidad mediante aportes voluntarios con recompensas + capas premium opcionales no esenciales. |
| Entidad | Atributos clave |
| --- | --- |
| Usuario | id, nombre, email, hash de autenticación, preferencias, estado de aportante (sí/no), número de fundador. |
| Movimiento | id, usuario_id, tipo (ingreso/egreso), valor, medio_pago, contraparte_raw, contraparte_id (FK a Agenda), categoría_id, fecha, fuente (notificación/OCR/manual/Open Finance), estado_confirmación. |
| Agenda financiera | id, usuario_id, número_o_cuenta, nombre_comercio, categoría_default, color, origen (manual/sugerido_comunidad). |
| Categoría | id, nombre, tipo (ingreso/egreso), ícono, es_personalizada. |
| Presupuesto | id, usuario_id, categoría_id, límite_mensual, mes_vigente. |
| Meta de ahorro | id, usuario_id, nombre, monto_objetivo, fecha_límite, monto_acumulado. |
| Regla de clasificación | id, patrón_regex, entidad_bancaria, categoría_sugerida, prioridad. |
| Aporte voluntario | id, usuario_id, monto, fecha, recompensa_otorgada. |
| Recompensa | Descripción |
| --- | --- |
| Huella financiera generativa | Pieza de arte generativo única, calculada a partir de los propios patrones de gasto del usuario (color por categoría, ritmo por día del mes, densidad por monto). Cada huella es irrepetible porque depende de los datos reales de cada persona; se puede usar como fondo de pantalla o imagen de perfil. |
| Temas exclusivos | Paletas de color e iconografía adicionales a la estética base (incluyendo un tema “fundador” terracota-ocre), sin afectar la funcionalidad para quien no aporta. |
| Resumen del año enriquecido | Todos los usuarios reciben un resumen anual de sus finanzas; los aportantes obtienen una versión más profunda, animada y exportable en alta calidad para compartir. |
| Capa | Tecnología |
| --- | --- |
| Mobile (MVP) | Kotlin nativo (acceso directo y estable a NotificationListenerService); arquitectura modular para aislar la lógica de negocio y facilitar una futura versión iOS/Swift o una capa compartida. |
| Backend / API | Node.js (NestJS) o Spring Boot; API REST versionada; capa de abstracción de “fuentes de datos” descrita en la sección 5.2. |
| Base de datos | PostgreSQL como almacenamiento principal; Redis para caché de sesión y agenda financiera de acceso frecuente. |
| Web | React o Next.js para el panel web. |
| Motor de clasificación (MVP) | Reglas + expresiones regulares por entidad bancaria, evaluadas on-device cuando sea posible (offline-first). |
| OCR | Librería on-device (ML Kit Text Recognition) para minimizar dependencia de servicios externos y costo variable. |
| Fase | Alcance | Entregables clave |
| --- | --- | --- |
| Fase 0 | Validación | Prototipo de captura de notificaciones + parseo de 3-4 entidades bancarias principales (Bancolombia, Nequi, Daviplata, Nu). Validación técnica del riesgo de Play Store. |
| Fase 1 (MVP) | Android, reglas + regex, gratis | Captura automática, agenda financiera, clasificación, dashboard, cronología, presupuestos, metas, confirmación ligera, aportes voluntarios con recompensas básicas (número de fundador, huella generativa). |
| Fase 2 | Robustecimiento | OCR de comprobantes, alertas y detección de patrones, exportación, panel web completo, aprendizaje comunitario opt-in de la agenda financiera. |
| Fase 3 | Multiplataforma | Versión iOS vía Share Extension, sincronización multi-dispositivo, capas premium para independientes. |
| Fase 4 | Open Finance | Integración oficial como tercero del Sistema de Finanzas Abiertas colombiano cuando la Superintendencia Financiera habilite el esquema voluntario para terceros no vigilados, reemplazando gradualmente la dependencia de notificaciones. |
| Riesgo | Mitigación |
| --- | --- |
| Rechazo o restricción en Google Play por permiso de notificaciones de alto riesgo. | Declaración transparente y detallada del uso del permiso; política de privacidad clara; minimizar el alcance del listener a solo las apps autorizadas; evitar cualquier uso de Accessibility API para acciones autónomas. |
| Cambios en el formato de notificaciones de apps bancarias rompen el parseo. | Arquitectura multi-fuente (sección 5.2) que no depende de un único canal; reglas de parseo versionadas y actualizables remotamente sin publicar nueva versión de la app. |
| iOS no permite leer notificaciones de terceros. | Captura vía Share Extension y OCR como mecanismos nativos de iOS desde la Fase 3; comunicación clara de que Android es la plataforma de lanzamiento. |
| Percepción de riesgo o desconfianza por tratarse de datos financieros sensibles. | Comunicación explícita de que la app nunca pide credenciales ni accede a cuentas directamente; cifrado y opciones reales de exportación/eliminación como argumento de marketing. |
| Modelo gratuito sin ingresos suficientes para sostener servidores. | Diseño offline-first que reduce el costo de backend en el MVP; monetización diversificada (aportes, premium para independientes, licenciamiento B2B) en vez de depender de una sola fuente. |