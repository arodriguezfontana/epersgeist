# TP 2 - HIBERNATE

Tras varios días en los que volcamos nuestro escaso conocimiento sobre aquellos seres y el supuesto nuevo mundo que conocimos en cada expedición todos nos encontrabamos reunidos a altas horas de la noche en llamada.

Algunos puliendo detalles de la representación creada durante la última semana, otros explorando foros olvidados como Reddit sobre ritualismo, y alguno preparandose un cafe mientras vagas ideas rondaban por nuestras cabezas.

Prueba, error, teorización e investigación guiaron nuestro accionar hasta un posteo hecho por un usuario hace años: 

```
    Ritualistas
--------------------------------------------------------------------------------  
Desconocidos, ¿alguna vez sintieron la presencia de otro ser pese a estar solos?
Confien, sé como se siente.
Los espero, http:/altiru.lev.di

    .rdj
```

Pese a leer este viejo posteo firmado por un usuario bajo las siglas RDJ despertó nuestra curiosidad así como incredulidad. Sin embargo, la sensación de estar leyendo algo más complejo que un simple posteo seguía latente.

Ese mensaje se sentía dirigido hacia nosotros, por lo que decidimos explorar el enlace de la publicación, el cual presentaba una página con varios pasos a seguir para _"revelar otra parte de la verdad"_

Tras el inicio del ritual, todos sentimos una repentina debilidad apoderandose de nuestros cuerpos hasta que luego de un parpadeo vimos en frente nuestro a otra entidad, la cual presentaba una extraña mezcla de rasgos demoníacos y angelicales esperandonos para darnos un breve mensaje

> _¿Ustedes... son los causantes del desequilibrio...?_

<p align="center">
  <img src="angel-y-demonio.jpeg" />
</p>

Tras el resonar de sus palabras una densa bruma comenzó a rodearnos mientras veaimos recuerdos del espiritu gracias a algún tipo de arte arcana hasta que nos despertamos en el mismo lugar donde realizamos el ritual, conscientes de nuestra proxima misión... 


## Cambios desde el TP anterior

Se identificaron una serie de cambios necesarios a hacerse sobre la prueba de concepto anterior: La capa de persistencia deberá cambiarse para utilizar Hibernate/JPA en lugar de JDBC.

**Nota:** No es necesario que mantengan los test y funcionalidad que utilizaron en el TP de JDBC.

## Funcionalidad

Los seis programadores vieron que la representación de los espíritus actual se quedaba corta, por lo cual había que profundizar mas en ella.
- Su tipo, puede ser Demoníaco, o Angelical.
- Todo espiritu puede o no estar conectado a un Medium. En caso de no estar conectado a ningún médium, se lo considera libre.

### Ubicación

Tanto los espíritus como los médiums están en una ubicación, la cual deberá crearse con un nombre que **NO** podrá repetirse, y que servira como refugio temporal para los mediums.

### Exorcismo

Un médium puede intentar exorcizar a otro médium para desligarlo de los espiritus demoníacos que el segundo posea, a coste de arriesgar a sus propios ángeles en el intento.

Para exorcisar a otro medium, el exorcista deberá de tener, al menos, un espiritu angelical conectado consigo. En caso de no tener ningún aliado de tipo Angel, deberá arrojarse la excepción `ExorcistaSinAngelesException`.

Si el médium exorcista cuenta con espíritus angelicales como aliados, se desencadena la siguente lógica para resolver el exorcismo:

Todos los ángeles conectados al médium exorcista tendrán un unico ataque contra un demonio del médium a exorcisar, y en cada ataque, deberá calcularse si el ataque del ángel es exitoso o no, para lo cual se tomara como base un número aleatorio entre el 1 y el 10, y a este número se le sumara el nivel de conexión del espiritu angelical, definiendo las chances de hacer un ataque exitoso de la siguiente manera:

```porcentaje de ataque exitoso = (random(1, 10)) + nivelDeConexion```

**Nota: si la suma de estos valores llegara a superar los 100 puntos, debe limitarse a 100**

Una vez calculado el porcentaje de exito del ataque, se debe verificar si este supera a un numero aleatorio entre 1 y 100 que representa la capacidad defensiva del demonio ante ese ataque, y en caso de ser superior a esa defensa, el ataque sera exitoso. Llevandolo a un ejemplo concreto

Si tenemos al angel Tyrael, el cual tiene 30 puntos de nivel de conexión, vinculado a un medium que intenta exorcizar a otro medium, el calculo de exito de un ataque de Tyrael se calculará como:

```porcentaje de ataque exitoso = (random(1, 10)) + 30```

Supongamos que el random nos devuelve 5, el porcentaje final sera 35.

¿Cómo se resuelve el resto del ataque? Se calcula el porcentaje de defensa del demonio, y entonces:

- Si el porcentaje de ataque es mayor al de defensa *(supongamos que fue 26)*, el demonio que recibio el ataque perdera una cantidad de nivel de conexión equivalente a la mitad del nivel de conexión que tenia el angel, en este ejemplo, 15 *(30 / 2)*, mientras el angel no sufre ningun efecto secundario.
- En caso contrario, si el porcentaje de defensa es igual o mayor que el de ataque, el angel perdera 5 puntos de conexion y el demonio no sufrira ninguna penalizacion.

Si durante el exorcismo un angel o demonio pierde todos sus puntos de nivel de conexion, el espiritu se desvinculará de su medium.

---

Visualmente, un flujo de un exorcismo se vería como:

```
Ángeles de un medium: [Rika (nivelDeConexion: 60), Ivaar (nivelDeConexion: 80), Hana (nivelDeConexion: 5)]
Demonios de otro medium [Jaeger (nivelDeConexion: 50), Noroi (nivelDeConexion: 66)]

-- 1er ataque:
Rika ataca a Jaeger, y supongamos que su ataque es exitoso 
>>> Jaeger pierde 30 puntos de conexión, quedando con 20.

-- 2do ataque:
Ivaar ataca a Jaeger, y supongamos que su ataque es exitoso
>>> Jaeger pierde 40 puntos de conexión, quedando con 0, por lo que deja de estar conectado a su medium.

-- 3er ataque:
Hana ataca a Noroi, y supongamos que su ataque falla
>>> Hana pierde 5 puntos de conexión, quedando con 0, por lo que deja de estar conectada a su medium.
```

## Servicios

Se pide que implementen los siguientes servicios los cuales serán consumidos por el equipo frontend de la aplicación.

### MediumService

- Métodos CRUD + `recuperarTodos`.

- `void descansar(Long mediumId)` - Dado un médium, recuperará 15 puntos de mana y cada espíritu conectado hacia él recuperará 5 puntos de conexión.

- `void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar)` - Dado dos médiums, el médium exorcista intentará exorcizar al otro con las reglas ya planteadas.

- `List<Espiritu> espiritus(Long mediumId)` - Dado un médium, retorna todos los espíritus con los que está conectado.

- `Espiritu invocar(Long mediumId, Long espirituId)` - Dado un médium y un espíritu, el médium deberá invocar al espíritu a su ubicación generandole un costo de 10 puntos de mana. Si el médium no tiene mana suficiente no hace nada. Si el espíritu no esta libre, lanzar una excepción.

### EspirituService

- Métodos CRUD + `recuperarTodos`.

- `Medium conectar(Long espirituId, Long mediumId)` - Deberá lograr que el espíritu y el médium queden conectados, y además fortalecer el nivel de conexión del espiritu con el 20% del mana del medium. Si no están en la misma ubicación o el espíritu no esta libre, lanzar una excepción.

- `List<Espiritu> espiritusDemoniacos()` - Retorna los espíritus demoníacos ordenados según su nivel de conexion en orden descendente.

### UbicacionService

- Métodos CRUD + `recuperarTodos`.

- `List<Espiritu> espiritusEn(Long ubicacionId)` - Retorna los espíritus existentes en la ubicación dada.

- `List<Medium> mediumsSinEspiritusEn(Long ubicacionId)` - Retorna los médiums posicionados en la ubicación dada que no hayan conectado con ningún espíritu.

### Se pide:
- Que provean implementaciones para las interfaces descriptas anteriormente.
- Que modifiquen el mecanismo de persistencia de los espíritus de forma de que todo el modelo persistente utilice Hibernate.
- Asignen propiamente las responsabilidades a todos los objetos intervinientes, discriminando entre servicios, DAOs y objetos de negocio.
- Creen test que prueben todas las funcionalidades pedidas, con casos favorables y desfavorables.
- Que los tests sean determinísticos. Hay mucha lógica que depende del resultado de un valor aleatorio. Se aconseja no utilizar directamente generadores de valores aleatorios (random) sino introducir una interfaz en el medio para la cual puedan proveer una implementación mock determinística en los tests.

### Recuerden que:
- Pueden agregar nuevos métodos y atributos a los objetos ya provistos.

### Consejos útiles:
- Finalicen los métodos de los services de uno en uno. Que quiere decir esto? Elijan un service, tomen el método más sencillo que vean en ese service, y encárguense de desarrollar la capa de modelo, de servicios y persistencia solo para ese único método. Una vez finalizado (esto también significa testeado), pasen al próximo método y repitan.
- Cuando tengan que persistir con Hibernate, analicen: Qué objetos deben ser persistentes y cuáles no? Cuál es la cardinalidad de cada una de las relaciones? Cómo mapearlas?

## Bonus: Paginación

## Implementacion bonus

Se nos pide agregar paginación al método `espiritusDemoniacos()` de EspirituService.

Se agregará a la firma de este método una página, una cantidad por página, y una dirección que puede ser: ASCENDENTE o DESCENDENTE.

- `List<Espiritu> espiritusDemoniacos(Direccion direccion, Int pagina, Int cantidadPorPagina)` - Retorna los espíritus demoníacos ordenados según su nivel de conexion, respetando la dirección, la página y cantidad declaradas en la firma del método.

Un ejemplo: espiritusDemoniacos(DESCENDENTE, 0, 5): Retorna los primeros 5 espíritus demoníacos con mayor nivel de conexión.
