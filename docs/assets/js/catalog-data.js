const CATEGORY_INFO = [
  { id: "verano", title: "Temporada de Verano", category: "Verano", page: "temporada.html", image: "assets/img/temporada.jpg", description: "Seleccion destacada para la temporada actual." },
  { id: "conjuntos-falda", title: "Conjuntos con Falda", category: "Conjuntos con Falda", page: "conjuntos-falda.html", image: "assets/img/conjuntosconfalda.png", description: "Looks coordinados pensados para venta inmediata." },
  { id: "conjuntos-pantalon", title: "Conjuntos con Pantalon", category: "Conjuntos con Pantalon", page: "conjuntos-pantalon.html", image: "assets/img/conjuntoconpantalon.png", description: "Sets versatiles con caida estructurada y comoda." },
  { id: "enterizos", title: "Enterizos", category: "Enterizos", page: "enterizos.html", image: "assets/img/enterizos.png", description: "Prendas protagonistas para catalogo y redes." },
  { id: "faldas", title: "Faldas", category: "Faldas", page: "faldas.html", image: "assets/img/faldas.png", description: "Modelos con enfoque comercial y combinable." },
  { id: "tops", title: "Tops", category: "Tops", page: "tops.html", image: "assets/img/tops.png", description: "Basicos y acentos visuales para distintas colecciones." },
  { id: "vestidos-cortos", title: "Vestidos Cortos", category: "Vestidos Cortos", page: "vestidos-cortos.html", image: "assets/img/vestidoscortos.png", description: "Opciones juveniles con alta rotacion." },
  { id: "vestidos-largos", title: "Vestidos Largos", category: "Vestidos Largos", page: "vestidos-largos.html", image: "assets/img/vestidoslargos.png", description: "Siluetas fluidas para ocasiones especiales." },
  { id: "otros-estilos", title: "Otros Estilos", category: "Otros Estilos", page: "otros-estilos.html", image: "assets/img/estilos.png", description: "Coleccion abierta para piezas fuera de linea principal." }
];

const SEED_ITEMS = [
  { id: "1001", code: "1001", title: "Look Verano 1001", category: "Verano", price1: "450", price2: "520", image: "assets/img/temporada.jpg" },
  { id: "1101", code: "1101", title: "Conjunto Falda 1101", category: "Conjuntos con Falda", price1: "490", price2: "560", image: "assets/img/conjuntosconfalda.png" },
  { id: "1201", code: "1201", title: "Conjunto Pantalon 1201", category: "Conjuntos con Pantalon", price1: "520", price2: "590", image: "assets/img/conjuntoconpantalon.png" },
  { id: "1301", code: "1301", title: "Enterizo 1301", category: "Enterizos", price1: "580", price2: "650", image: "assets/img/enterizos.png" },
  { id: "1401", code: "1401", title: "Falda 1401", category: "Faldas", price1: "340", price2: "390", image: "assets/img/faldas.png" },
  { id: "1501", code: "1501", title: "Top 1501", category: "Tops", price1: "260", price2: "310", image: "assets/img/tops.png" },
  { id: "1601", code: "1601", title: "Vestido Corto 1601", category: "Vestidos Cortos", price1: "540", price2: "610", image: "assets/img/vestidoscortos.png" },
  { id: "1701", code: "1701", title: "Vestido Largo 1701", category: "Vestidos Largos", price1: "690", price2: "760", image: "assets/img/vestidoslargos.png" },
  { id: "1801", code: "1801", title: "Estilo Especial 1801", category: "Otros Estilos", price1: "470", price2: "540", image: "assets/img/estilos.png" }
];

const FIREBASE_DB_URL = "https://distribuidora-carols-default-rtdb.firebaseio.com";
