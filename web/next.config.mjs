/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  // El panel no sirve imagenes remotas: los comprobantes de factura no se suben
  // a la nube (solo su URI local en el telefono). Ver backend/README.md.
  images: { remotePatterns: [] }
};

export default nextConfig;
