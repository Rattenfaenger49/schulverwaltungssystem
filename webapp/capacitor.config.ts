import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'school_system.app-v1',
  appName: 'school_system',
  webDir: 'dist/school_system_frontend',
  server: {
    androidScheme: 'http',
  },
  plugins: {
    CapacitorHttp: {
      enabled: true
    }
  }
};

export default config;
