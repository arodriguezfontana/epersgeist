# TP 6 \- ElasticSearch  

## INTRO

Cruzaron el portal, todos sus sentidos en alerta; ojos ciegos en la oscuridad, las gotas de sudor frío comenzaban a perlar sus sienes. El cuerpo tenso, las manos en puños, a la espera de lo peor.

De repente, un sonido infernal, un estruendo inconmensurable, los hizo perder otro sentido. Ensordecidos, con un pitido irritante perforando sus mentes, sintieron cómo algo los empujaba, primero hacia un lado, luego hacia arriba, como si una gran ráfaga de viento los envolviera, elevándolos de forma violenta y arremolinada, tan velozmente que los dejó sin aliento.

Estaban totalmente aturdidos, desorientados, incapaces de tomar una bocanada de aire. Comenzaban a sentir el latir de sus corazones: erráticos, galopantes, desesperados, eufóricos pero inútiles. No había manera de que el oxígeno llegara a sus pulmones. El ritmo comenzaba a decaer. Seguían atrapados en ese remolino infernal sin ver, sin escuchar, sin respirar. Perdían la conciencia.

Lo único que percibían con seguridad era el latido de sus corazones.

*Latido.&nbsp;&nbsp;&nbsp;Latido.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Latido.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Latido.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Latido…*  
<br><br>
 *Silencio.*

<br><br>
<br><br>
<br><br>
<br><br>
<br><br>
<br><br>
<br><br>
<br><br>
<br><br>
<br><br>
---
Despertaron aturdidos y desorientados, sin noción alguna de cuánto tiempo había transcurrido. No reconocían el lugar donde se encontraban: una especie de pasillo iluminado por una pequeña vela, de modo que no conseguían definir sus dimensiones, pero sí podían observar una puerta a pocos metros de la lumbre.

Luego de unos minutos, consiguieron ponerse en pie. Seguían algo aturdidos, pero de a poco sus mentes se despejaban y paulatinamente recobraban sus sentidos.

Del otro lado de la puerta se escuchaba una voz; no lograban distinguir palabras. De repente, sin explicación alguna, cruzaron miradas, y los cinco entendieron que debían atravesar esa puerta. Algo en su interior los movía hacia ella, de manera magnética, hipnótica. Así lo hicieron.

—Bienvenidos, hermanos —dijo una voz diabólica.

Una figura grotesca se erguía en medio de la habitación, iluminada por una luz roja: cuernos y alas negras, cola de serpiente, patas de cabra. ¿Era, acaso, el mismísimo diablo? Instintivamente, sus cuerpos reaccionaron ante el peligro que emanaba del desconocido. La piel se les erizó instantáneamente. Tiesos, inmóviles, sus pies parecían haberse clavado al piso. Pánico manifiesto en sus rostros.

—¿Quién sos? —gritó aterrado uno de los jóvenes.

La criatura solo los miraba y sonreía de manera perturbadora, mostrando sus enormes dientes en punta. Los ojos, amarillos, bien abiertos, con la mirada perdida.

—¿Dónde estamos? ¿Quién sos? —preguntó otro programador con voz gutural, llena de desesperación.

Como respuesta, el satánico ser comenzó a reírse despacio, luego más fuerte, hasta llegar a la carcajada más histérica que jamás habían oído.

—¡Marcelito\! —gritó alguien al otro lado del recinto.

Se encendió la luz.

—¿Otra vez haciendo show? No te das cuenta de que los asustás —dijo un sujeto de aspecto gentil. Él acababa de prender las luces—. Mil disculpas, este pibe es un desubicado —agregó mientras se acercaba al grupo de jóvenes—. Yo soy Tomi, él es Marcelito, y les damos la bienvenida.

Sobre Tomi flotaba un halo resplandeciente; de él emanaba un aura de paz. Rápidamente sintieron cómo sus cuerpos se relajaban al oír su cálida voz. De manera grupal, pudieron sentir confianza absoluta. Si estaban con Tomi, todo iba a estar bien.

La criatura de nombre Marcelito les hizo un gesto de disculpa. Ahora ya no les parecía tan aterrador. Seguía teniendo una presencia inquietante, pero no había rastro de la hostilidad que sintieron en su primera impresión.

—¿Pero dónde estamos? —preguntó, un poco más tranquilo, uno de los cinco.

—¡Ah, sí\! Están en el mundo espiritual, y por si no se dieron cuenta… están muertos.

Atónitos ante la revelación, los aventureros tomaron un instante para asimilar lo que estaba sucediendo. Al colocar una mano en su pecho, pudieron percibir que el retumbar de sus corazones se había callado. No había nada. Quietud absoluta.

De inmediato, una nueva revelación: algunos de ellos manifestaban unos pequeños cuernos en sus cabezas, otros una tenue aureola flotante. Definitivamente, lo que decía el confiable Tomi era cierto.

Todo lo conocido hasta el momento se perdía en el vacío. Una nueva era daba su inicio.

**¿Podrán nuestros aventureros persistir en esta nueva realidad?**

<br><br>
## Cambios en nuestra aplicación

## Descripciones  
En esta nueva vida extracorpórea, la capacidad de interpretar el mundo de nuestros mediu…espíritus? ha cambiado de forma drástica, por lo que ahora su entendimiento de todo aquello que los rodea les permite conocer secretos fuera el alcance de cualquier mente mortal. 

Todas las entidades antes conocidas ahora poseen una descripción que detalla sus características más importantes.

## Ubicaciones  
En este nuevo plano las ubicaciones se presentan de forma distinta frente a los ojos de nuestros médiums, difusas, distorsionadas. Sin embargo, al irse acostumbrando a sus nuevas habilidades, con el tiempo son capaces de interpretar sus secretos, por más espesa que sea la niebla.

Ahora los médiums pueden investigar ubicaciones según sus descripciones, partiendo de palabras clave y a pesar de que están algo equivocados en su interpretación.    

## Teletransportación 
Despegados de los límites de la carne, nuestros nuevos espíritus mantienen su capacidad de movimiento independiente entre ubicaciones, solo que ya no necesitan caminar, simplemente se transportan a ellas. Esto les permite ignorar las limitaciones de distancia previas. 

## Servicios  
Realizar el CRUD correspondiente para espíritus, médiums y ubicaciones para que se persistan en ElasticSearch incluyendo sus descripciones. Queda en los alumnos determinar que de esta información es necesaria en cada base de datos previa.

## EspirituService 

* `teletransportar(Long idUbicacion, Long idEspiritu)` \- Cambia la ubicación y la coordenada del espíritu a la ubicación designada. El espíritu tiene que estar libre y tener más de 50 de nivel de conexión. Transportarse consume 5 puntos. 

## MediumService

* `List\<UbicacionElastic\> investigarUbicaciones(String criterioDeBusqueda)` \- Devuelve una lista de ubicaciones que contengan la palabra recibida por parámetro en su descripción   
* `List\<UbicacionElastic\> investigarUbicacionesMejoradoFuzzines(String criterioDeBusqueda, String fuzzines)`- Devuelve una lista de ubicaciones que contengan todas las palabras que llegan por parámetro. Recibe un String indicando el valor de “fuzzines”, es decir, que tan mal escritas pueden estar las palabras usadas como criterio. Para esto investigar que valores permite Elastic en este campo.  

## UbicacionService  
Para mayor simplicidad a la hora de implementar otros servicios, las ubicaciones persistidas en Elastic serán representadas por una sola coordenada en lugar de un polígono. Modificar la creación de ubicaciones acorde a esto.

## Bonus 1: Registro de teletransporte  
Se nos pide tener un registro de los movimientos realizados por estos nuevos espíritus. Para ello será necesario persistir una instancia de “historialElastic” cada vez que un espíritu se teletransporte. Se debe poder determinar el espíritu que realizó dicha acción, la ubicación de destino y en qué fecha se realizó el movimiento.  
En EspirituService:

* `HistorialTeletransportacion historialDeMovimiento(Long espirituId)` \- Retorna una instancia de historialDeMovimiento que contiene el nombre del espíritu, la ubicación que más frecuentó, los ids de todas las ubicaciones en las que estuvo y la cantidad de movimientos realizados.     

## Bonus 2: Búsqueda semántica  
Se nos pide mejorar una más las capacidades de búsqueda de nuestra aplicación permitiendo la búsqueda semántica, es decir, poder encontrar médiums en este caso por medio de una descripción que se asemeje a la que tienen pero que no posea textualmente las mismas palabras. Para ello investigar la búsqueda por comparación de vectores y apoyarse con lenguaje Python para generar scripts que permitan la transformación de palabras a vectores.

BusquedaSemantica  
`void indexar(MediumElastic medium)` \- Agrega un índice al médium para que sea posible el funcionamiento de la búsqueda.

`List\<Long\> buscar(String texto)` \- Retorna una lista con los ids de los médiums que coinciden con el parámetro de búsqueda.

   

 
