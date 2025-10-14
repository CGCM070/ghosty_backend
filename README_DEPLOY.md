# 🚀 Guía de Deploy - Ghosty Backend

## 📋 Datos de Configuración

### Frontend (Vercel):
```
URL Production: https://ghostyform.vercel.app
```

### Base de Datos (Neon PostgreSQL):
```
Host:     ep-round-sun-ago8jfh9-pooler.c-2.eu-central-1.aws.neon.tech
Port:     5432
Database: neondb
Username: neondb_owner
SSL Mode: require
```

### Repositorio:
```
GitHub: https://github.com/CGCM070/ghosty_backend
```

---

## 🐳 PASO 1: Build Local (Testing)

### Prerrequisitos:
- Docker instalado
- Java 21 (opcional, solo si quieres probar sin Docker)

### Build de la imagen:
```bash
cd ghosty-backend

# Build de la imagen Docker
docker build -t ghosty-backend:local .

# Esto toma 2-3 minutos la primera vez
```

### Test local:
```bash
# Crear archivo .env con tus variables
cp .env.example .env

# Editar .env con tus valores reales
nano .env

# Ejecutar el contenedor
docker run --rm -p 8080:8080 \
  --env-file .env \
  ghosty-backend:local

# La app estará disponible en http://localhost:8080
```

### Verificar que funciona:
```bash
# Health check
curl http://localhost:8080/actuator/health

# Debería responder:
# {"status":"UP"}
```

---

## 📦 PASO 2: Push al Registry de GitHub

### 2.1. Hacer push del código a GitHub:
```bash
# Asegúrate de estar en la rama main
git checkout main

# Add todos los archivos nuevos
git add .

# Commit
git commit -m "feat: Add Docker configuration and CI/CD"

# Push a GitHub
git push origin main
```

### 2.2. GitHub Actions se ejecutará automáticamente:
1. Ve a: https://github.com/CGCM070/ghosty_backend/actions
2. Verás el workflow "Build & Push Docker Image" ejecutándose
3. Espera ~5 minutos a que termine
4. La imagen se publicará en: `ghcr.io/cgcm070/ghosty_backend:latest`

### 2.3. Verificar que la imagen se creó:
```bash
# Login a GitHub Container Registry
echo $GITHUB_TOKEN | docker login ghcr.io -u CGCM070 --password-stdin

# Pull de la imagen
docker pull ghcr.io/cgcm070/ghosty_backend:latest

# Ejecutar la imagen
docker run --rm -p 8080:8080 \
  --env-file .env \
  ghcr.io/cgcm070/ghosty_backend:latest
```

---

## 🚀 PASO 3: Deploy a Render

### 3.1. Crear Web Service en Render:

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Click en **"New +"** → **"Web Service"**
3. Selecciona **"Deploy an existing image from a registry"**

### 3.2. Configuración del servicio:

```
Image URL: ghcr.io/cgcm070/ghosty_backend:latest

Name: ghosty-backend
Region: Frankfurt (o el que prefieras)
Instance Type: Free (o Starter $7/mes)
```

### 3.3. Configurar Variables de Entorno:

En la sección "Environment Variables", añade:

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Database (Neon)
DB_HOST=ep-round-sun-ago8jfh9-pooler.c-2.eu-central-1.aws.neon.tech
DB_PORT=5432
DB_NAME=neondb
DB_USERNAME=neondb_owner
DB_PASSWORD=npg_nB4C9ZEuoaAf

# JWT Secret (usa el generado)
JWT_SECRET=rtCCdZves4m6q0cf5dFwqUWcT9bh1/2ptyYfkPcDiT3l0lyoBIzjS+FGgP7/oHmjhs6MN/Q9w+jnJsj77In8TQ==

# Google OAuth
GOOGLE_CLIENT_ID=612785658051-n5o1gi30ded8jl4bc46laaie3hlkojvs.apps.googleusercontent.com

# CORS
CORS_ALLOWED_ORIGINS=https://ghostyform.vercel.app

# Puerto (Render lo asigna automáticamente, pero por si acaso)
PORT=8080
```

### 3.4. Health Check Path (opcional):
```
Health Check Path: /actuator/health
```

### 3.5. Deploy:
- Click en **"Create Web Service"**
- Render comenzará a desplegar tu imagen
- Espera 2-3 minutos

### 3.6. Obtener la URL del backend:
Render te dará una URL como:
```
https://ghosty-backend-xxxx.onrender.com
```

---

## 🔄 PASO 4: Actualizar el Frontend

### 4.1. Actualizar environment.prod.ts:

En tu proyecto frontend (`ghosty-frontend/src/environments/environment.prod.ts`):

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://ghosty-backend-xxxx.onrender.com/v1/api', // ← Tu URL de Render
  googleClientId: '612785658051-n5o1gi30ded8jl4bc46laaie3hlkojvs.apps.googleusercontent.com'
};
```

### 4.2. Rebuild y redeploy del frontend:
```bash
cd ghosty-frontend
npm run build
# Vercel detectará el cambio y redesplegará automáticamente
```

O manualmente en Vercel:
1. Ve al dashboard de Vercel
2. Click en tu proyecto
3. Deployments → Redeploy

---

## ✅ PASO 5: Testing de Producción

### 5.1. Verificar el backend:
```bash
# Health check
curl https://ghosty-backend-xxxx.onrender.com/actuator/health

# Test de CORS
curl -H "Origin: https://ghostyform.vercel.app" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -X OPTIONS \
  https://ghosty-backend-xxxx.onrender.com/v1/api/auth/login
```

### 5.2. Test desde el frontend:
1. Abre: https://ghostyform.vercel.app
2. Intenta hacer login
3. Verifica que funcione la autenticación
4. Intenta Google login

### 5.3. Verificar la base de datos:
```bash
# Conectarse a Neon
psql 'postgresql://neondb_owner:npg_nB4C9ZEuoaAf@ep-round-sun-ago8jfh9-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require'

# Ver las tablas creadas
\dt

# Ver usuarios
SELECT id, username, email FROM "user";

# Salir
\q
```

---

## 🔄 PASO 6: Actualización Continua (CI/CD)

### Workflow automático:
Cada vez que hagas push a `main`:
1. GitHub Actions construye la imagen Docker
2. La sube a GitHub Container Registry
3. Render puede configurarse para auto-deploy desde el registry

### Deploy manual desde Render:
1. Ve a tu servicio en Render
2. Click en "Manual Deploy" → "Deploy latest commit"

### Forzar rebuild de la imagen:
```bash
# Hacer cambios en el código
git add .
git commit -m "feat: Nueva funcionalidad"
git push origin main

# GitHub Actions se ejecutará automáticamente
```

---

## 🐛 Troubleshooting

### Error: "Unable to connect to database"
```bash
# Verificar variables de entorno en Render
# Asegúrate que DB_PASSWORD no tenga espacios

# Test de conexión directa
psql 'postgresql://neondb_owner:npg_nB4C9ZEuoaAf@ep-round-sun-ago8jfh9-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require'
```

### Error: "CORS policy"
```bash
# Verificar que CORS_ALLOWED_ORIGINS incluya tu URL exacta
# En Render, actualizar la variable:
CORS_ALLOWED_ORIGINS=https://ghostyform.vercel.app
```

### Error: "JWT validation failed"
```bash
# Verificar que JWT_SECRET esté configurado
# Debe ser el mismo que generaste
```

### Error: "Google OAuth failed"
```bash
# Verificar que GOOGLE_CLIENT_ID esté correcto
# En Google Cloud Console, verificar que las URLs estén autorizadas:
# - https://ghostyform.vercel.app
# - https://ghosty-backend-xxxx.onrender.com
```

### Build de Docker falla:
```bash
# Limpiar cache y rebuilder
docker system prune -a
docker build --no-cache -t ghosty-backend:local .
```

### Render se duerme (Free tier):
- El tier gratuito de Render se duerme después de 15 minutos de inactividad
- Primera petición después de dormir toma ~30 segundos
- Solución: Upgrade a Starter ($7/mes) o usar servicio de "ping"

---

## 📊 Monitoreo

### Logs en Render:
1. Ve a tu servicio en Render
2. Click en "Logs"
3. Verás los logs en tiempo real

### Métricas:
- Render dashboard muestra CPU, memoria, requests
- Neon dashboard muestra conexiones, queries, storage

---

## 🔐 Seguridad

### ✅ Buenas prácticas implementadas:
- ✅ Contraseñas en variables de entorno (no en código)
- ✅ JWT secret único para producción
- ✅ SSL/TLS habilitado (Neon requiere sslmode=require)
- ✅ Usuario no-root en Docker
- ✅ CORS configurado correctamente
- ✅ Health checks activos

### ⚠️ Recomendaciones adicionales:
- Rotar JWT_SECRET periódicamente
- Monitorear logs de accesos
- Configurar rate limiting (opcional)
- Backup de base de datos (Neon lo hace automáticamente)

---

## 📞 Comandos Útiles

### Ver imagen en GitHub:
```bash
https://github.com/CGCM070/ghosty_backend/pkgs/container/ghosty_backend
```

### Logs de Render:
```bash
# Desde dashboard de Render o usando su CLI
render logs -s ghosty-backend
```

### Recrear base de datos (si es necesario):
```sql
-- Conectarse a Neon
psql 'postgresql://...'

-- Eliminar todas las tablas
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

-- Las tablas se recrearán automáticamente al reiniciar el backend
```

---

## 🎉 ¡Felicidades!

Tu aplicación está en producción:
- ✅ Frontend: https://ghostyform.vercel.app
- ✅ Backend: https://ghosty-backend-xxxx.onrender.com
- ✅ Database: Neon PostgreSQL (serverless)
- ✅ CI/CD: GitHub Actions
- ✅ Registry: GitHub Container Registry

---

## 📚 Próximos Pasos (Opcional)

### PLAN B: Native Image (Optimización)
Si quieres mejorar el rendimiento:
- Compilación nativa con GraalVM
- Arranque ultra-rápido (~0.1s)
- Menor uso de memoria (~150MB)
- Ver: `PLAN_DOCKERIZACION_Y_DEPLOY.md` → Fase 2

### Mejoras adicionales:
- [ ] Implementar rate limiting
- [ ] Agregar logging centralizado
- [ ] Configurar alertas de monitoreo
- [ ] Implementar cache con Redis
- [ ] Agregar tests automatizados en CI/CD
- [ ] Documentación de API con Swagger

---

**¿Necesitas ayuda con algo? ¡Estoy aquí!** 🚀
