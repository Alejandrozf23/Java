package Documentos;

import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class Principal extends javax.swing.JFrame {
    private String pathIn = "";
    private String pathOut = "";
    private String concetp = "";
    private final String path_aux = "report/";
    private final ArrayList<File> files = new ArrayList<>();
    private final ArrayList<XMLFile> xmlfiles = new ArrayList<>();
    
    public Principal() {
        initComponents();
        this.setTitle("Generador de Reportes");
        this.setLocationRelativeTo(null);
        getProperties();
    }
    
    private void getProperties(){
        InputStream is = null;
        Properties properties = new Properties();
        try{
            is = new FileInputStream("\\properties\\app.properties");
            properties.load(is);
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        pathIn = properties.getProperty("entrada");
        pathOut = properties.getProperty("salida");
    }
    
    
    private void listFolder(File carpeta){ //Función para listar los archivos
        for(File fichero: carpeta.listFiles()) {
            if(fichero.isDirectory()) //El archivo, es un directorio. Se llama a la función recursivamente.
                listFolder(fichero);
            else //Es un archivo, falta validar extensión.
                if(getFileExtension(fichero).equals(".xml")) //Comprueba si es un archivo .xml
                   files.add(fichero); //Si es correcto, agrega el archivo al ArrayList.
        }
    }
    
    private String getFileExtension(File file){ //Función para obtener la extensión de los archivos. 
        String extension = "";
        try{
            if(file != null && file.exists()){
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        }catch(Exception except){
            extension = "";
            System.err.println(except.getMessage());
        }
        return extension;
    }
    
    private void readXML(String path){ //Función para leer el archivo XML
        String tipoComp,serie,fecha,lugarExp,numSello,folioInt;
        String cliente,clientRFC,domicilioFis,entregaA;
        String pagoCond,pagoForma,pagoMeth,tipoRel,uuidRel,cfdiUso,monedaTipo,periodoFac;
        String contrato,fianza,casaAfin,noProvee,noRemision,noPedido,cambioTipo;
        String codigo,lote,cantidad,claveU,claveSP,unidad,observaciones,codeQR;
        String folioFiscal,certificadoSAT,fechaCert,cadenaOriginal,selloDigitalE,selloDigitalSAT;
        String noContrato = "",vigencia = "",dateContrato = "",valContrato = "";
        double precioU,descuento,total;
        ArrayList<Products> productos = new ArrayList<>();
        try{
            File file = new File(path);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);            
            String namefile = file.getName().substring(0,file.getName().lastIndexOf("."));
            /************************************************************************************/ //Datos de la parte superior derecha        
            tipoComp = getDataAttribute("cfdi:Comprobante","TipoDeComprobante",document)+" INGRESO";
            serie = getDataAttribute("cfdi:Comprobante","Serie",document);
            fecha = getDataAttribute("cfdi:Comprobante","Fecha",document);
            lugarExp = getDataAttribute("cfdi:Comprobante","LugarExpedicion",document);
            numSello = getDataAttribute("cfdi:Comprobante","NoCertificado",document);
            folioInt = getDataAttribute("empresa:PACIENTE","VIT27",document);
            /************************************************************************************/ //Datos del cliente
            cliente = getDataAttribute("cfdi:Receptor","Nombre",document);
            clientRFC = getDataAttribute("cfdi:Receptor","Rfc",document);
            domicilioFis = getDataAttribute("empresa:PACIENTE","VIT32",document);
            entregaA = getDataAttribute("empresa:ENTREG","comodin6",document)+" "+getDataAttribute("empresa:ENTREG","Calle",document)+
                       " "+getDataAttribute("empresa:ENTREG","Municipio",document)+" "+getDataAttribute("empresa:ENTREG","Estado",document)+
                       " "+getDataAttribute("empresa:ENTREG","Pais",document)+" "+getDataAttribute("empresa:ENTREG","Cod_Postal",document);
            /************************************************************************************/ //Datos de la forma de pago
            pagoCond = getDataAttribute("cfdi:Comprobante","CondicionesDePago",document);
            pagoForma = (getDataAttribute("cfdi:Comprobante","FormaPago",document).equals("99"))?"99 - POR DEFINIR":"";
            pagoMeth = (getDataAttribute("cfdi:Comprobante","MetodoPago",document).equals("PPD"))?"PPD - PAGO EN PARCIALIDADES O DIFERIDO":"";
            tipoRel = ""; //NO se encontró el atributo solicitado
            uuidRel = ""; //NO se encontró el atributo solicitado
            cfdiUso = (getDataAttribute("cfdi:Receptor","UsoCFDI",document).equals("P01"))?"P01 - POR DEFINIR":"";
            monedaTipo = getDataAttribute("cfdi:Comprobante","Moneda",document);
            periodoFac = getDataAttribute("empresa:PACIENTE","VIT3",document);
            /************************************************************************************/ //Datos del contratos
            contrato = getDataAttribute("empresa:PACIENTE","VIT4",document);
            fianza = getDataAttribute("empresa:PACIENTE","VIT5",document);
            casaAfin = getDataAttribute("empresa:PACIENTE","VIT6",document);
            noProvee = ""; //NO se encontró el atributo solicitado
            noRemision = getDataAttribute("empresa:PACIENTE","VIT1",document);
            noPedido = getDataAttribute("empresa:PACIENTE","VIT2",document);
            cambioTipo = getDataAttribute("cfdi:Comprobante","TipoCambio",document);
            observaciones = getDataAttribute("empresa:PACIENTE","VIT13",document);
            /************************************************************************************/ //Datos fiscales
            folioFiscal = getDataAttribute("tfd:TimbreFiscalDigital","UUID",document);
            certificadoSAT = getDataAttribute("tfd:TimbreFiscalDigital","NoCertificadoSAT",document);
            fechaCert = getDataAttribute("tfd:TimbreFiscalDigital","FechaTimbrado",document);
            selloDigitalE = getDataAttribute("tfd:TimbreFiscalDigital","SelloCFD",document);
            selloDigitalSAT = getDataAttribute("tfd:TimbreFiscalDigital","SelloSAT",document);
            cadenaOriginal = "||"+getDataAttribute("tfd:TimbreFiscalDigital","Version",document)+"|"+getDataAttribute("tfd:TimbreFiscalDigital","UUID",document)+
                        "|"+getDataAttribute("tfd:TimbreFiscalDigital","FechaTimbrado",document)+"||"+getDataAttribute("tfd:TimbreFiscalDigital","SelloCFD",document)+
                        "||"+getDataAttribute("tfd:TimbreFiscalDigital","NoCertificadoSAT",document)+"||";
            codeQR = "https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?id="+getDataAttribute("tfd:TimbreFiscalDigital","UUID",document)+
                    "&re="+getDataAttribute("cfdi:Emisor","Rfc",document)+"&rr="+clientRFC+"&tt"+getDataAttribute("cfdi:Comprobante","Total",document);
            /************************************************************************************/ //Datos de los conceptos por factura
            NodeList list = document.getElementsByTagName("cfdi:Concepto");
            NodeList lists = document.getElementsByTagName("empresa:INFCONCEP");
            for(int i = 0; i<list.getLength() ;i++){
                String descripcion;
                codigo = getDataAttribute("NoIdentificacion",list,i); 
                lote = getDataAttribute("Orden_de_venta",lists,i); 
                Node node = list.item(i); getDescription(node);
                descripcion = getDataAttribute("Descripcion",list ,i)+concetp;
                cantidad = getDataAttribute("Cantidad",list,i);
                claveU = getDataAttribute("ClaveUnidad",list,i);
                claveSP = getDataAttribute("ClaveProdServ",list,i);
                unidad = getDataAttribute("Unidad",list,i);
                precioU = Double.parseDouble(getDataAttribute("ValorUnitario",list,i));
                descuento = 0.0;
                total = precioU*Double.parseDouble(cantidad);
                noContrato = ""; //NO se encontró el atributo solicitado
                vigencia = ""; //NO se encontró el atributo solicitado
                String date[] = getDataAttribute("empresa:PACIENTE","VIT3",document).split("PERIODO\\ DEL\\ ");
                String aux_date = (date.length == 2)?date[1]:"";
                String aux_comp = getDataAttribute("cfdi:Comprobante","Fecha",document).split("T")[0].replaceAll("-","/");
                dateContrato = "CON FECHA DEL "+aux_date+", SE RECIBIÓ EL SERVICIO E INSUMOS QUE AMPARA LA PRESENTE FACTURA No. "
                        + "FA- DE FECHA "+aux_comp+" POR LA EMPRESA "+getDataAttribute("cfdi:Emisor","Nombre",document).toUpperCase();
                valContrato = getDataAttribute("empresa:PACIENTE","VIT29",document);
                productos.add(new Products(codigo,lote,descripcion,cantidad,claveU,claveSP,unidad,precioU,descuento,total));
            }
            /************************************************************************************/
            xmlfiles.add(new XMLFile(namefile,tipoComp,serie,fecha,lugarExp,numSello,folioInt,cliente,
                    clientRFC,domicilioFis,entregaA,pagoCond,pagoForma,pagoMeth,tipoRel,uuidRel,cfdiUso,
                    monedaTipo,periodoFac,contrato,fianza,casaAfin,noProvee,noRemision,noPedido,cambioTipo,
                    observaciones,folioFiscal,certificadoSAT,fechaCert,cadenaOriginal,selloDigitalE,selloDigitalSAT,
                    codeQR,noContrato,vigencia,dateContrato,valContrato,productos));
        }catch(ParserConfigurationException | IOException | SAXException except){
            System.out.println(except.getMessage());
        }
    }
    
    private void getDescription(Node node){ //Función recursiva para encontrar el nodo final de la descripción.
        NodeList nodelist = node.getChildNodes();
        if(nodelist.getLength()>0){
            for(int i = 0; i<nodelist.getLength() ;i++)
                if(nodelist.item(i).getNodeType() == Node.ELEMENT_NODE)
                    getDescription(nodelist.item(i));
        }else{
            if(node.getNodeType() == Node.ELEMENT_NODE){
                String descripcion = "";
                Element element = (Element)node;
                descripcion += "\n      Base: "+element.getAttribute("Base")+"      Impuesto: "+element.getAttribute("Impuesto");
                descripcion += " - IVA"+"       Tipo Factor: "+element.getAttribute("TipoFactor");
                descripcion += "\n      Tasa/Cuota: "+element.getAttribute("TasaOCuota")+"      Importe: "+element.getAttribute("Importe")+"\n";
                concetp = descripcion;
            }            
        }
    }
    
    private String getDataAttribute(String fieldName , NodeList list , int i){ //Función para obtener el atributo requerido.
        String attribute = "";
        Node node = list.item(i);
        if(node.getNodeType() == Node.ELEMENT_NODE){
            Element element = (Element)node;
            attribute = element.getAttribute(fieldName); //FieldName indica el atributo solicitado.
        }
        return attribute;
    }
    
    private String getDataAttribute(String nodeName , String fieldName , Document document){ //Función para obtener el atributo requerido.
        String attribute = "";
        NodeList list = document.getElementsByTagName(nodeName); //NodeName indica el Nodo donde se encuentra el atributo.
        for(int i = 0; i<list.getLength() ;i++){
            Node node = list.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element)node;
                attribute = element.getAttribute(fieldName); //FieldName indica el atributo solicitado.
            }
        }
        return attribute;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tituloJL = new javax.swing.JLabel();
        readJB = new javax.swing.JButton();
        generateJB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tituloJL.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        tituloJL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tituloJL.setText("Reportes PDF");

        readJB.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        readJB.setText("LEER XML");
        readJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readJBActionPerformed(evt);
            }
        });

        generateJB.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        generateJB.setText("GENERAR REPORTE");
        generateJB.setEnabled(false);
        generateJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateJBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tituloJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(readJB, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                        .addComponent(generateJB)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tituloJL, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(readJB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generateJB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void readJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readJBActionPerformed
        xmlfiles.clear(); files.clear();
        listFolder(new File(pathIn));
        if(files.size()>0){ //Existen al menos, 1 documento XML
            for(int i = 0; i<files.size() ;i++){                
                readXML(files.get(i).getPath());
            }
            generateJB.setEnabled(true);
        }else{
            JOptionPane.showMessageDialog(this,"La carpeta no tienen ningún archivo XML");
        }
    }//GEN-LAST:event_readJBActionPerformed

    private void generateJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateJBActionPerformed
        for(int i = 0; i<xmlfiles.size() ;i++){
            try {
                PDFReport report = new PDFReport(xmlfiles.get(i));
                report.createReport(path_aux);
                report.manipulatePdf(path_aux+xmlfiles.get(i).getNamefile()+".pdf",pathOut+xmlfiles.get(i).getNamefile()+".pdf");
                File fichero = new File(path_aux+xmlfiles.get(i).getNamefile()+".pdf"); fichero.delete();
            } catch (IOException | DocumentException ex) {
                System.out.println(ex.getMessage()); 
            }
        }
        JOptionPane.showMessageDialog(this,"Proceso exitoso, los pdf se encuentran en la carpeta 'salida'");
    }//GEN-LAST:event_generateJBActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new Principal().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton generateJB;
    private javax.swing.JButton readJB;
    private javax.swing.JLabel tituloJL;
    // End of variables declaration//GEN-END:variables
}
