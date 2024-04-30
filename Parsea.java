

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.google.gson.Gson;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parsea {
    public static void main(String[] args) {
        try { 
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            Document doc = db.parse(new File("src\\plantilla.xml"));
            
            mostrarJugadoresTitulares(doc);
            
            Plantilla plantilla=new Plantilla();
            
            cargarTecnicos(doc, plantilla);
            cargarJugadores(doc, plantilla);            
            System.out.println("La plantilla es:\n"+plantilla);
            
            jugadorMasCaro(doc);          
            
            generaJSON(plantilla);
            
            addTecnico (doc, "Jesus", "Preparador físico"); 
            
            generaXml (doc);
          

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

   
    // Recupera de plantilla.xml los jugadores titulares y los muestra por consola
    public static void mostrarJugadoresTitulares (Document doc){
   	
        NodeList jugadores= doc.getElementsByTagName("jugador");
       
        System.out.println("TITULARES:\n");

        for (int i = 0; i < jugadores.getLength(); i++) {

            if(jugadores.item(i).getNodeType() ==Node.ELEMENT_NODE){
            	Element e = (Element) jugadores.item(i);
                String perfil= e.getAttribute("perfil");
                
                if (perfil.equals("titular")) {
                    String nombre =e.getElementsByTagName("nombre").item(0).getTextContent();
                    String dorsal =e.getElementsByTagName("dorsal").item(0).getTextContent();
                    String demarcacion =e.getElementsByTagName("demarcacion").item(0).getTextContent();
                    String ficha =e.getElementsByTagName("ficha").item(0).getTextContent();

                    System.out.println("Nombre: " + nombre+ ","+" Dorsal: "+ dorsal+ "," +" Demarcación: " + demarcacion+ ","+" Ficha: " +ficha);
                }
            	
            }
        }
    }

 
 //Carga en el objeto plantilla la relación de técnicos obtenidos de plantilla.xml
 public static void cargarTecnicos (Document doc, Plantilla plantilla){
 	
     NodeList tecnicos= doc.getElementsByTagName("tecnico");
    
     for (int i = 0; i < tecnicos.getLength(); i++) {
    	 Node tecnicoNodo=tecnicos.item(i);
    	 
    	 if(tecnicoNodo.getNodeType()==Node.ELEMENT_NODE) {
    		 Element e= (Element)tecnicoNodo;
    		String nombre=e.getElementsByTagName("nombre").item(0).getTextContent();
    		String puesto= e.getElementsByTagName("puesto").item(0).getTextContent();
    		
    		Tecnico te=new Tecnico();
    		te.setNombre(nombre);
    		te.setPuesto(puesto);
    		plantilla.addTecnico(te);
    	 }
                            
     }
   
 }

 
//Carga en el objeto plantilla la relación de jugadores obtenidos de plantilla.xml
 public static void cargarJugadores (Document doc, Plantilla plantilla){
 	
     NodeList jugadores= doc.getElementsByTagName("jugador");
    
     for (int i = 0; i < jugadores.getLength(); i++) {

         NodeList hijos = jugadores.item(i).getChildNodes();
         
         Node jugNod = jugadores.item(i);
         

	    if (jugNod.getNodeType() == Node.ELEMENT_NODE) {
       				
	    for (int j = 0; j < hijos.getLength(); j++) {
	    	
	     Element jugEle=(Element) hijos;
           
         String nombre=jugEle.getElementsByTagName("nombre").item(0).getTextContent();
         String dor=jugEle.getElementsByTagName("dorsal").item(0).getTextContent();
         int dorsal=dor.lastIndexOf(dor);
         String demarcacion =jugEle.getElementsByTagName("demarcacion").item(0).getTextContent();
         String fi=jugEle.getElementsByTagName("ficha").item(0).getTextContent();
         double ficha=fi.lastIndexOf(fi);
         
         Jugador jugador=new Jugador();
         
         jugador.setNombre(nombre);
         jugador.setDorsal(dorsal);
         jugador.setDemarcacion(demarcacion);
         jugador.setFicha(ficha);
         
         plantilla.addJugador(jugador);
		}	    
         
	   }
     }
 }

//Muestra por consola el nombre y la ficha del jugador que tiene la ficha más alta
public static void jugadorMasCaro(Document doc){

     Double fichaActual=0.0;
     Double fichaMayor=0.0;
     String jugadorMasCaro="";

     int posCaro=0;
                
     NodeList fichas= doc.getElementsByTagName("ficha");
     for (int i = 0; i < fichas.getLength(); i++) {
	     Element ele=(Element) doc.getElementsByTagName("jugador").item(i);
    	 fichaActual=Double.parseDouble(fichas.item(i).getTextContent());
    	 if (fichaActual>fichaMayor) {
        		fichaMayor=fichaActual;
 	            jugadorMasCaro = ele.getElementsByTagName("nombre").item(0).getTextContent();
        	 } 		 
        }
        
     System.out.println("\nEl jugador que más cobra es:\n"+jugadorMasCaro+" con una ficha de: "+fichaMayor+ " millones de euros.\n");
 }

// Genera un JSON con la información del objeto plantilla que recibe como parámetro 
public static void generaJSON (Plantilla plantilla){

	 Gson gson = new Gson();

	 String json = gson.toJson(plantilla);

	 // Muestro el resultado
	 System.out.println("El JSON es:\n"+json);

	 // Escribo el fichero
	 try (BufferedWriter bw = new BufferedWriter(new FileWriter("src\\plantilla.json"))) {
		 bw.write(json);
		 System.out.println("Fichero creado");
	 } catch (IOException ex) {
		 Logger.getLogger(Parsea.class.getName()).log(Level.SEVERE, null, ex);
	 }

}



//Añade un técnico al fichero XML (da igual en qué posición). No es necesario añadirlo también al objeto plantilla.  
	public static void addTecnico (Document doc,  String nombre, String puesto){
		Element root = doc.getDocumentElement();
		Element tecnico = doc.createElement("tecnico");
        
		Element nombreElemento=doc.createElement("tecnico");
		nombreElemento.appendChild(doc.createTextNode(nombre));
		tecnico.appendChild(nombreElemento);
		Element puestoElemento=doc.createElement("puesto");
		puestoElemento.appendChild(doc.createTextNode(puesto));
		tecnico.appendChild(puestoElemento);

		root.appendChild(tecnico);     

    }   
	


	//Genera un nuevo XML a partir del doc que recibe por parámetro. AQUI NO HAY QUE COMPLETAR NADA
    public static void generaXml (Document doc){
        try {
            
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer archivo_nuevo = tf.newTransformer();

            archivo_nuevo.setOutputProperty( OutputKeys.INDENT, "yes" );
            archivo_nuevo.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            archivo_nuevo.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
            archivo_nuevo.setOutputProperty( OutputKeys.METHOD, "xml" );
            archivo_nuevo.setOutputProperty("http://www.oracle.com/xml/is-standalone", "yes");

            Element root = doc.getDocumentElement();
            DOMSource origenDOM = new DOMSource(root);

            File nuevo_doc = new File("src\\nueva_plantilla.xml");
            StreamResult destinoDOM = new StreamResult(nuevo_doc);

            archivo_nuevo.transform(origenDOM, destinoDOM);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    
}
