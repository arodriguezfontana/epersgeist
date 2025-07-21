## 🖥️ Scriptcito de contribuciones 📊

Como saben, les pedimos que todos contribuyan de manera pareja durante el desarrollo del TP, por lo que les armamos un pequeño scriptcito para ver los insights dentro de su repo _(pues los de GitHub funcionan sobre main y pushear a main en EPERS equivale a un pecado capital :P)_

---

### ¿Como usarlo?
Veran que en la raiz de su proyecto ahora tambien tienen una carpeta `contributions` la cual posee un script `contributions.sh` que pueden usar para generar un archivo que contenga los insights de como laburaron.

Para usarlo, tienen que abrir una terminal bash desde la carpeta `contributions` _(por ejemplo, la terminal de GitBash que les incluye git cuando lo instalan)_ y correr el comando:

```bash
./contributions.sh <rama> [inicio]
```

¿Que hace esto? Corre el script para analizar la rama que le pasan por parametro al ejecutarlo **(deben pasarle SI o SI una rama)** desde la fecha de inicio, con formato `yyyy-mm-dd`, que le pasen _(la fecha pueden no pasarsela y en ese caso evaluara desde las ultimas 2 semanas)_

Esto les generara un archivo `stats_timestamp.json` el cual será el log de como trabajaron durante ese tiempo en esa branch, mostrando 2 listas:

- La primera que listara por usuario las estadisticas de cada usuario de la siguiente forma:

```json
{
    "👤 Contributor": "username",
    "📊 Stats": {
        "📨 commits": 0,
        "✅ added": 0,
        "⛔ deleted": 0
    },
    "💬 Messages": [...]
}
```

- La segunda que listara los commits que tiraron directamente a la branch sobre la que trabajaron. Esto no es relevante **SALVO** que ejecuten el script sobre dev / develop, en ese caso les sirve para ver si alguno omitio los pasos del gitflow _(y amerita ser juzgado hasta el final de la cursada 🥴)_

---

No es necesario que lo usen todos los dias para trackear su progreso, pero se los damos como herramienta para que lo usen cuando quieran saber como vienen, ademas de que nosotros probablemente lo corramos cuando hagan entregas.