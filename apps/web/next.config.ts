import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  transpilePackages: ['@salonflow/ui', '@salonflow/types'],
};

export default nextConfig;
