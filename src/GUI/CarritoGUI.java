package GUI;

import Analizadores.Cup.parser;
import Analizadores.Flex.Lexer;
import Controllers.ArchivoController;
import Model.Instruccion;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import panamahitek.Arduino.PanamaHitek_Arduino;

/**
 *
 * @author jpmazate
 */
public class CarritoGUI extends javax.swing.JFrame {

    PanamaHitek_Arduino arduino;
    ArrayList<Instruccion> listaInstrucciones;
    File file;
    long inicio = 0, fin = 0;
    boolean bandera = false;
    String ultimaInstruccion = "", ultimaVelocidad = "ALTA";

    public CarritoGUI() throws Exception {
        initComponents();
        arduino = new PanamaHitek_Arduino();
        
        setPanelControl(true);
    }

    public void abrir() {
        JFileChooser buscadorArchivos = new JFileChooser();
        int opcion = buscadorArchivos.showOpenDialog(this);
        // si se acepta el archivo entra
        if (opcion == JFileChooser.APPROVE_OPTION) {
            String archivo = buscadorArchivos.getSelectedFile().getAbsolutePath();
            String archivo1 = buscadorArchivos.getSelectedFile().toString();
            // si se encuentra el archivo pide el tiempo en milisegundos
            System.out.println("Se ha encontrado el archivo: " + archivo1);
            try {

                File file = new File(archivo1);
                if (file.exists()) {
                    ArchivoController carga = new ArchivoController(file);
                    this.file = file;
                    carga.leerArchivo(this.areaComandos);
                    asignarNombreArchivo(this.file.getName());
                }

            } catch (Exception e) {
                System.out.println("Hubo algun error");

            }
        } else if (opcion == JFileChooser.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(null, "No se ha cargado ningun archivo");
        }
        try {
            // crea el archivo que se selecciono

        } catch (Exception e) {
            System.out.println("No se selecciono ningun archivo");
        }
    }

    public void asignarNombreArchivo(String nombre) {
        this.labelArchivo.setText("Archivo: " + nombre);
    }

    public void guardar() {
        if (file == null) {
            file = guardarComo();
            if (file != null) {
                asignarNombreArchivo(this.file.getName());
            }
        } else {
            ArchivoController carga = new ArchivoController(file);
            carga.guardarArchivo(file, areaComandos);
        }

    }

    public File guardarComo() {
        ArchivoController carga = new ArchivoController(file);
        File file = carga.guardarComoArchivo(areaComandos);
        return file;
    }

    public void conectarBluetooth() {
        String puerto = puertoBluetooth.getText();
        if (puerto == null || puerto.equals("")) {
            JOptionPane.showMessageDialog(null, "EL PUERTO ESTA VACIO, ESPECIFIQUE UN POR FAVOR");
            setPanelControl(false);
        } else {
            if (!puerto.startsWith("COM")) {
                JOptionPane.showMessageDialog(null, "EL PUERTO NO INICIA CON LA PALABRA RESERVADA \"COM\", POR FAVOR INTRODUZCALO");
                setPanelControl(false);
            } else {
                if (puerto.length() <= 3) {
                    JOptionPane.showMessageDialog(null, "EL PUERTO NO TIENE UN NUMERO DE PUERTO, ESPECIFIQUE UNO POR FAVOR");
                    setPanelControl(false);
                } else {
                    try {
                        int valorPuerto = Integer.parseInt(puerto.substring(3));
                        System.out.println(valorPuerto);
                        try {
                            arduino.arduinoTX("/dev/ttyACM1", 9600);
                            this.labelBluetooth.setText("Conectado al puerto: " + puerto);
                            setPanelControl(true);
                        } catch (UnsatisfiedLinkError e) {
                            JOptionPane.showMessageDialog(this, "EL PUERTO " + puerto + " NO LOGRA CONECTARSE CON EL MODULO BLUETOOTH DEL CARRITO, VERIFIQUE EL PUERTO COM AL QUE ESTA CONECTADO EL MODULO BLUETOOTH POR FAVOR");
                            setPanelControl(false);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "EL PUERTO INDICADO NO LOGRA CONECTARSE CON EL MODULO BLUETOOTH DEL CARRITO, VERIFIQUE EL PUERTO COM AL QUE ESTA CONECTADO EL MODULO BLUETOOTH POR FAVOR");
                            setPanelControl(false);
                        }

                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "EL VALOR INGRESADO DESPUES DE LA PALABRA RESERVADA \"COM\" NO ES DE TIPO NUMERICO ENTERO POR FAVOR INTRODUZCA UN NUMERO ENTERO");
                        setPanelControl(false);
                    }
                }
            }
        }
    }

    public void setPanelControl(boolean value) {
        this.panelControl.setVisible(value);
    }

    public void generarInstruccion(long tiempo) {
        int valor = (int) tiempo;
        if (!ultimaVelocidad.equals("")) {
            if (ultimaInstruccion.equals("")) {
                areaComandos.append("INSTRUCCION(" + "PARAR" + "," + valor + "," + ultimaVelocidad + ")\n");
            } else {
                areaComandos.append("INSTRUCCION(" + ultimaInstruccion + "," + valor + "," + ultimaVelocidad + ")\n");
            }

        }
    }

    public void enviarInstruccionArduino(String caracter) {
        try {

            if (bandera) {
                fin = System.currentTimeMillis();
                generarInstruccion(fin - inicio);
                inicio = fin;
            } else {
                inicio = System.currentTimeMillis();
                bandera = true;

            }

            // this.arduino.sendData(caracter);
        } catch (UnsatisfiedLinkError e) {
            JOptionPane.showMessageDialog(this, "EL PUERTO INDICADO NO LOGRA CONECTARSE CON EL MODULO BLUETOOTH DEL CARRITO, VERIFIQUE EL PUERTO COM AL QUE ESTA CONECTADO EL MODULO BLUETOOTH POR FAVOR");
            setPanelControl(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "EL PUERTO INDICADO NO LOGRA CONECTARSE CON EL MODULO BLUETOOTH DEL CARRITO, VERIFIQUE EL PUERTO COM AL QUE ESTA CONECTADO EL MODULO BLUETOOTH POR FAVOR");
            setPanelControl(false);

        }
    }

    public void agregarInstruccion() {
        String instruccion = comandoTextField.getText();
        if (instruccion == null || instruccion.equals("")) {
            JOptionPane.showMessageDialog(null, "LA INSTRUCCION ESTA VACIA POR FAVOR INGRESE UNA INSTRUCCION VALIDA");
        } else {
            areaComandos.append(instruccion + "\n");
        }
    }

    public void operarComandos(List<Instruccion> lista) throws InterruptedException {
        try {
            for (int i = 0; i < lista.size(); i++) {

                Instruccion instr = lista.get(i);
                arduino.sendData(instr.getVelocidad());
                arduino.sendData(instr.getTipoMovimiento());
                Thread.sleep(instr.getTiempo());
            }
            arduino.sendData("c");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "EL PUERTO INDICADO NO LOGRA CONECTARSE CON EL MODULO BLUETOOTH DEL CARRITO, VERIFIQUE EL PUERTO COM AL QUE ESTA CONECTADO EL MODULO BLUETOOTH POR FAVOR");
        } catch (UnsatisfiedLinkError e) {
            JOptionPane.showMessageDialog(this, "EL PUERTO INDICADO NO LOGRA CONECTARSE CON EL MODULO BLUETOOTH DEL CARRITO, VERIFIQUE EL PUERTO COM AL QUE ESTA CONECTADO EL MODULO BLUETOOTH POR FAVOR");
        }
    }

    public void ejecutarInstrucciones() {
        try {
            String texto = areaComandos.getText();
            StringReader reader = new StringReader(texto);
            Lexer lex = new Lexer(reader, areaComandos);
            parser parser = new parser(lex, areaComandos);
            parser.parse();
            operarComandos(parser.getListaInstrucciones());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR EJECUTAR LOS COMANDOS");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        puertoBluetooth = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        labelBluetooth = new javax.swing.JLabel();
        panelControl = new javax.swing.JPanel();
        apagado = new javax.swing.JButton();
        cargarComandos = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        comandoTextField = new javax.swing.JTextField();
        velocidadAlta = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaComandos = new javax.swing.JTextArea();
        ejecutarComandos = new javax.swing.JButton();
        labelArchivo = new javax.swing.JLabel();
        atras = new javax.swing.JButton();
        velocidadMedia = new javax.swing.JButton();
        adelante = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        derecha = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        izquierda = new javax.swing.JButton();
        velocidadBaja = new javax.swing.JButton();
        guardarComandos = new javax.swing.JButton();
        encendido = new javax.swing.JButton();
        parar = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Ingrese el puerto a utilizar del Bluetooth:");

        puertoBluetooth.setText("COM2");

        jButton1.setText("Conectar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        labelBluetooth.setText("Estado Bluetooth: No conectado");

        apagado.setText("Apagado");
        apagado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apagadoActionPerformed(evt);
            }
        });

        cargarComandos.setText("CargarComandos");
        cargarComandos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarComandosActionPerformed(evt);
            }
        });

        jLabel3.setText("Comando a ingresar:");

        velocidadAlta.setText("Velocidad Alta");
        velocidadAlta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                velocidadAltaActionPerformed(evt);
            }
        });

        areaComandos.setColumns(20);
        areaComandos.setRows(5);
        jScrollPane1.setViewportView(areaComandos);

        ejecutarComandos.setText("Ejecutar Comandos");
        ejecutarComandos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ejecutarComandosActionPerformed(evt);
            }
        });

        labelArchivo.setText("Archivo: ninguno");

        atras.setText("Atras");
        atras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atrasActionPerformed(evt);
            }
        });

        velocidadMedia.setText("Velocidad Media");
        velocidadMedia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                velocidadMediaActionPerformed(evt);
            }
        });

        adelante.setText("Adelante");
        adelante.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adelanteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                adelanteMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                adelanteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                adelanteMouseReleased(evt);
            }
        });
        adelante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adelanteActionPerformed(evt);
            }
        });

        jLabel4.setText("Area Comandos:");

        derecha.setText("Derecha");
        derecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                derechaActionPerformed(evt);
            }
        });

        jButton2.setText("GuardarComoComandos");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        izquierda.setText("Izquierda");
        izquierda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                izquierdaActionPerformed(evt);
            }
        });

        velocidadBaja.setText("Velocidad Baja");
        velocidadBaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                velocidadBajaActionPerformed(evt);
            }
        });

        guardarComandos.setText("GuardarComandos");
        guardarComandos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarComandosActionPerformed(evt);
            }
        });

        encendido.setText("Encendido");
        encendido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encendidoActionPerformed(evt);
            }
        });

        parar.setText("Parar");
        parar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pararActionPerformed(evt);
            }
        });

        jButton3.setText("Agregar Instruccion");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelControlLayout = new javax.swing.GroupLayout(panelControl);
        panelControl.setLayout(panelControlLayout);
        panelControlLayout.setHorizontalGroup(
            panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelControlLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(guardarComandos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cargarComandos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ejecutarComandos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(220, 220, 220))
            .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelControlLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelControlLayout.createSequentialGroup()
                            .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelControlLayout.createSequentialGroup()
                                    .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(apagado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(encendido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(168, 168, 168)
                                    .addComponent(izquierda)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(adelante)
                                        .addGroup(panelControlLayout.createSequentialGroup()
                                            .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(parar, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                                                .addComponent(atras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(derecha))))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelControlLayout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addGap(18, 18, 18)
                                    .addComponent(comandoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                            .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(velocidadMedia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(velocidadAlta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(velocidadBaja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addContainerGap())
                        .addGroup(panelControlLayout.createSequentialGroup()
                            .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addGroup(panelControlLayout.createSequentialGroup()
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(labelArchivo)))
                            .addContainerGap()))))
        );
        panelControlLayout.setVerticalGroup(
            panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelControlLayout.createSequentialGroup()
                .addContainerGap(194, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(90, 90, 90)
                .addComponent(ejecutarComandos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cargarComandos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guardarComandos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(16, 16, 16))
            .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelControlLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(adelante)
                        .addComponent(velocidadAlta)
                        .addComponent(encendido))
                    .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelControlLayout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(izquierda)
                                .addComponent(parar)
                                .addComponent(derecha))
                            .addGap(18, 18, 18)
                            .addComponent(atras))
                        .addGroup(panelControlLayout.createSequentialGroup()
                            .addGap(26, 26, 26)
                            .addComponent(velocidadMedia)
                            .addGap(26, 26, 26)
                            .addComponent(velocidadBaja))
                        .addGroup(panelControlLayout.createSequentialGroup()
                            .addGap(27, 27, 27)
                            .addComponent(apagado)))
                    .addGap(40, 40, 40)
                    .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(comandoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(1, 1, 1)
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelControlLayout.createSequentialGroup()
                            .addComponent(labelArchivo)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addComponent(jScrollPane1))
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelBluetooth)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jLabel1)
                        .addGap(33, 33, 33)
                        .addComponent(puertoBluetooth, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(80, Short.MAX_VALUE))
            .addComponent(panelControl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(puertoBluetooth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelBluetooth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        conectarBluetooth();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cargarComandosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarComandosActionPerformed
        abrir();
    }//GEN-LAST:event_cargarComandosActionPerformed

    private void adelanteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adelanteMouseClicked

    }//GEN-LAST:event_adelanteMouseClicked

    private void adelanteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adelanteMouseEntered

    }//GEN-LAST:event_adelanteMouseEntered

    private void adelanteMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adelanteMousePressed

    }//GEN-LAST:event_adelanteMousePressed

    private void adelanteMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adelanteMouseReleased

    }//GEN-LAST:event_adelanteMouseReleased

    private void adelanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adelanteActionPerformed
        enviarInstruccionArduino("a");
        this.ultimaInstruccion = "ADELANTE";
    }//GEN-LAST:event_adelanteActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        guardarComo();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void guardarComandosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarComandosActionPerformed
        guardar();
    }//GEN-LAST:event_guardarComandosActionPerformed

    private void izquierdaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_izquierdaActionPerformed
        enviarInstruccionArduino("b");
        this.ultimaInstruccion = "IZQUIERDA";
    }//GEN-LAST:event_izquierdaActionPerformed

    private void pararActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pararActionPerformed
        enviarInstruccionArduino("c");
        this.ultimaInstruccion = "PARAR";
        generarInstruccion(0);
        inicio = 0;
        this.bandera = false;
    }//GEN-LAST:event_pararActionPerformed

    private void derechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_derechaActionPerformed
        enviarInstruccionArduino("d");
        this.ultimaInstruccion = "DERECHA";
    }//GEN-LAST:event_derechaActionPerformed

    private void atrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atrasActionPerformed
        enviarInstruccionArduino("e");
        this.ultimaInstruccion = "ATRAS";
    }//GEN-LAST:event_atrasActionPerformed

    private void encendidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_encendidoActionPerformed
        enviarInstruccionArduino("f");
        this.ultimaInstruccion = "ENCENDER";
    }//GEN-LAST:event_encendidoActionPerformed

    private void apagadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apagadoActionPerformed
        enviarInstruccionArduino("g");
        this.ultimaInstruccion = "APAGAR";
        generarInstruccion(0);
        inicio = 0;
        this.bandera = false;
    }//GEN-LAST:event_apagadoActionPerformed

    private void velocidadAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_velocidadAltaActionPerformed
        enviarInstruccionArduino("h");
        this.ultimaVelocidad = "ALTA";
        this.ultimaInstruccion = "PARAR";
        generarInstruccion(0);
        inicio = 0;
        this.bandera = false;
    }//GEN-LAST:event_velocidadAltaActionPerformed

    private void velocidadMediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_velocidadMediaActionPerformed
        enviarInstruccionArduino("i");
        this.ultimaVelocidad = "MEDIA";
        this.ultimaInstruccion = "PARAR";
        generarInstruccion(0);
        inicio = 0;
        this.bandera = false;
    }//GEN-LAST:event_velocidadMediaActionPerformed

    private void velocidadBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_velocidadBajaActionPerformed
        enviarInstruccionArduino("j");
        this.ultimaVelocidad = "BAJA";
        this.ultimaInstruccion = "PARAR";
        generarInstruccion(0);
        inicio = 0;
        this.bandera = false;
    }//GEN-LAST:event_velocidadBajaActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        agregarInstruccion();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void ejecutarComandosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ejecutarComandosActionPerformed
        ejecutarInstrucciones();

    }//GEN-LAST:event_ejecutarComandosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton adelante;
    private javax.swing.JButton apagado;
    private javax.swing.JTextArea areaComandos;
    private javax.swing.JButton atras;
    private javax.swing.JButton cargarComandos;
    private javax.swing.JTextField comandoTextField;
    private javax.swing.JButton derecha;
    private javax.swing.JButton ejecutarComandos;
    private javax.swing.JButton encendido;
    private javax.swing.JButton guardarComandos;
    private javax.swing.JButton izquierda;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelArchivo;
    private javax.swing.JLabel labelBluetooth;
    private javax.swing.JPanel panelControl;
    private javax.swing.JButton parar;
    private javax.swing.JTextField puertoBluetooth;
    private javax.swing.JButton velocidadAlta;
    private javax.swing.JButton velocidadBaja;
    private javax.swing.JButton velocidadMedia;
    // End of variables declaration//GEN-END:variables
}
