// Path: src/app/app.config.ts
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter, withDebugTracing } from '@angular/router';
import { routes } from './app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http'; // <-- Import new providers
import { jwtInterceptor } from './core/interceptors/jwt-interceptor';
import { MatSnackBarModule } from '@angular/material/snack-bar';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes), 
    provideAnimations(),
    // THE FIX: Provide HttpClient with the interceptor
    provideHttpClient(withInterceptors([jwtInterceptor])),
    importProvidersFrom(MatSnackBarModule) 
  ]
};