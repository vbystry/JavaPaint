import javax.swing.*;
import javax.swing.border.Border;
import java.awt.color.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.*;

//Klasa z main()
public class KubiPaint{
    public static void main(String[] args) {
        myFrame f = new myFrame();
        f.setVisible(true);
        f.panel.repaint();
    }
}

//Rozszeżona klasa JPanel o metody i klasy do obsługi rysowania
class myPanel extends JPanel{
    private ArrayList<Shape> figureList = new ArrayList<Shape>();        
    private ArrayList<Color> colorList = new ArrayList<Color>();
    public Shape activeShape;           
    public menuPanel menu;
    public JPopupMenu popup;
    public boolean erFlag=false;

    myPanel(){
        super();
        //Tworzenie menu aktywnej figury
        //oraz słuchacza zdarzeń
        popup = new JPopupMenu();
        ActionListener menuListener = new ActionListener() {
            //Oprogramowanie metody wywoływanej przez
            //wykonanie akcji w menu aktywnej figury
            public void actionPerformed(ActionEvent event) {
                    switch(event.getActionCommand()){          
                    case "Set color to gray":
                        colorList.set(colorList.size()-1, Color.GRAY);
                        break;
                    case "Set color to black":
                        colorList.set(colorList.size()-1, Color.BLACK);
                        break;
                    case "Set color to red":
                        colorList.set(colorList.size()-1, Color.RED);
                        break;
                    case "Set color to pink":
                        colorList.set(colorList.size()-1, Color.PINK);
                        break;
                    case "Set color to orange":
                        colorList.set(colorList.size()-1, Color.ORANGE);
                        break;
                    case "Set color to yellow":
                        colorList.set(colorList.size()-1, Color.YELLOW);
                        break;
                    case "Set color to green":
                        colorList.set(colorList.size()-1, Color.GREEN);
                        break;
                    case "Set color to blue":
                        colorList.set(colorList.size()-1, Color.BLUE);  
                        break;
                    case "Delete figure":
                        figureList.remove(figureList.size()-1);
                        colorList.remove(colorList.size()-1);
                        activeShape = null;
                        activeShape = null;
                        break;
                }
                //Metoda repaint() powinna być wywoływana przy 
                //kazdej zmianie aby uytkownik ją płynnie widział
                repaint(); 
            }
          };
        JMenuItem item;
        popup.add(item = new JMenuItem("Set color to gray"));
        item.setHorizontalTextPosition(JMenuItem.LEFT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Set color to black"));
        item.setHorizontalTextPosition(JMenuItem.LEFT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Set color to red"));
        item.setHorizontalTextPosition(JMenuItem.LEFT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Set color to pink"));
        item.setHorizontalTextPosition(JMenuItem.LEFT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Set color to orange"));
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Set color to yellow"));
        item.setHorizontalTextPosition(JMenuItem.LEFT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Set color to green"));
        item.setHorizontalTextPosition(JMenuItem.LEFT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Set color to blue"));
        item.setHorizontalTextPosition(JMenuItem.LEFT);
        item.addActionListener(menuListener);
        popup.addSeparator();
        popup.add(item = new JMenuItem("Delete figure"));
        item.addActionListener(menuListener);
        
        //Dodawanie słuchacza zdarzeń myszki
        myMouseAdapter A= new myMouseAdapter();
        addMouseListener(A);
        addMouseMotionListener(A);
        addMouseWheelListener(A);
        this.menu = new menuPanel(this);
        this.add(menu);
        this.add(popup);

    }

    //Metoda dodająca "pustą" figurę do listy figur
    public void addFigure(String type, Point2D begin){                 
        switch(type){
            case "Rect":
                myRectangle r= new myRectangle();
                r.setFrameFromDiagonal(begin, begin);
                figureList.add(r);
                break;
            case "Circle":               
                myCircle o= new myCircle();
                o.setFrameFromDiagonal(begin, begin);
                o.width = Math.max(o.width, o.height);
                o.height = o.width;
                figureList.add(o);
                break;
            case "Triangle":
                myTriangle t = new myTriangle();
                t.addPoint((int)begin.getX(), (int) begin.getY());
                t.addPoint((int)begin.getX(), (int) begin.getY());
                figureList.add(t);
                break;
        }
        //Domyślnym kolorem figury jest szary
        colorList.add(Color.GRAY);
    }

    //metoda wywoływana przy aktualizacji grafiki
    private void doDrawing(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        //Rysowanie po kolei kazdej figury
        //z listy wraz z jej kolorem
        for(int i=0; i<figureList.size(); i++)
        {
            g2d.setColor(colorList.get(i));
            g2d.fill(figureList.get(i));
        }
        //Wyróznianie aktywnej figury
        if(activeShape!=null)
        {
            g2d.setColor(new Color(0,0,0));
            g2d.draw(activeShape);
        }
        //Fragment odpowiadający za widoczność linii
        //podczas wybierania drugiego wierzchołka trójkąta
        //try poniewaz figureList moze być pusta
        try {
            if(figureList.get(figureList.size()-1) instanceof myTriangle)
        {
            myTriangle t = (myTriangle)figureList.get(figureList.size()-1);
            if(t.lineFlag)  {g2d.drawLine(t.xpoints[0], t.ypoints[0], t.xpoints[1], t.ypoints[1]);}
        }   
        } catch (Exception e) {
            
        }
          
    }

    @Override
    public void paintComponent(Graphics g) {       
        super.paintComponent(g);
        doDrawing(g);  
    }

    //Wewnętrzna klasa obsługująca zdarzenia myszy
    private class myMouseAdapter extends MouseAdapter implements MouseMotionListener , MouseWheelListener{
        //Deklaracja zmiennych uzywanych przez
        //wewnetrzne metody słuchacza
        private boolean movingFlag;
        private Point2D catchVect;
        boolean drawingFlag = false;
        boolean popupFlag = false;

        @Override
        public void mousePressed(MouseEvent event) {
            Point p = event.getPoint();
            //menu.Frag jest parametrem mówiącym czy uzytkownik 
            //wybrał w menu z figurami figurę do narysowania
            if(menu.Flag!=null)
            {
                if(event.getButton() == MouseEvent.BUTTON1)
                {
                    //drawingFlag jest ustawiana gdy zatwierdzi się rysowanie figury
                    if(drawingFlag==false)
                    {
                        switch(menu.Flag){
                            case "Prostokat":
                                addFigure("Rect", event.getPoint());
                                drawingFlag = true;
                                break;
                            case "Kolo":
                                addFigure("Circle", event.getPoint());
                                drawingFlag=true;
                                break;
                            case "Trojkat":
                                addFigure("Triangle", event.getPoint());
                                drawingFlag = true;
                                break;
                        }
                    }
                    else
                    {
                        switch(menu.Flag){
                            case "Prostokat":
                                drawingFlag = false;
                                menu.Flag=null;
                                break;
                            case "Kolo":
                                drawingFlag = false;
                                menu.Flag = null;
                                break;
                            case "Trojkat":
                                myTriangle t = (myTriangle) figureList.get(figureList.size()-1);
                                if(t.npoints <3)
                                {
                                    t.addPoint(event.getX(), event.getY());
                                }
                                else   
                                {
                                    drawingFlag = false;
                                    menu.Flag = null;
                                }
                                break;
                        }
                    }
                }
                //Kliknięcie ppm przerywa rysowanie
                else if(drawingFlag)
                {
                    figureList.remove(figureList.size()-1);
                    drawingFlag = false;
                    menu.Flag = null;
                    
                }    
                
            }
            //Instrukcje wykonywane gdy wybrana jest aktywna figura 
            //oraz w nią klinięto
            else if(figureList.contains(activeShape) && activeShape.contains(p))
            {
                if (event.isPopupTrigger()) {
                    popup.show(myPanel.this, event.getX(), event.getY());
                }
                else 
                {
                    //Włączenie trybu poruszania oraz pobranie punktu zaczepienia
                    movingFlag = true; 
                    double x=0;
                    double y=0;
                    if(activeShape instanceof myRectangle){
                        
                        x=event.getX()- ((myRectangle)activeShape).x;
                        y=event.getY() - ((myRectangle)activeShape).y;
                    }
                    if(activeShape instanceof myCircle){
                        x=event.getX() - ((myCircle)activeShape).x;
                        y=event.getY() - ((myCircle)activeShape).y;
                        
                    }
                    if(activeShape instanceof myTriangle){
                        x=event.getX();
                        y=event.getY();
                        
                    }
                    catchVect =(Point2D)new Point((int)x,(int)y);
                }
                
                return;
            }
            else
            {
                //Gdy kliknięto w figurę ustawianie jej jako aktywnej
                //pozycja w liście figury aktywnej jest zmieniana na ostatnią
                //i zmienna jest dekrementowana dla uzyskania efektu "wyrózniania"
                for (int i = figureList.size()-1; i>=0; i--) {
                    if(figureList.get(i).contains(event.getX(), event.getY()))
                    {
                        
                        activeShape = figureList.get(i);
                        figureList.remove(activeShape);
                        figureList.add(activeShape);
                        Color c = colorList.get(i);
                        colorList.remove(i);
                        colorList.add(c);
                        repaint();
                        return;
                    }
                }
            }
            //jeśli zaden z warunkow nie jest spełniony 
            //figura aktywna jest odznaczana jeśli istnieje    
            activeShape = null;
            
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            //Wyjście z trybu przesuwania przy puszczeniu myszy
            movingFlag = false;
            
            repaint();

        }

        @Override
        public void mouseDragged(MouseEvent event) {
            //Jesli włączony jest tryb poruszania figura 
            //podąza za kursorem do czasu puszczenia myszy
            if (movingFlag) {
                int x=event.getX();
                int y=event.getY();
                Point2D movePoint =(Point2D)new Point((int)(x -catchVect.getX()),(int) (y - catchVect.getY()));
                if(activeShape instanceof myRectangle) {((myRectangle)activeShape).move(movePoint);}
                if(activeShape instanceof myCircle) {((myCircle)activeShape).move(movePoint);}
                if(activeShape instanceof myTriangle) {
                    int movex = (int)(event.getX() - catchVect.getX());
                    int movey = (int)(event.getY() - catchVect.getY());
                    catchVect = event.getPoint();
                    ((myTriangle)activeShape).translate(movex, movey);
                }
                repaint();
            }
            
        }

        public void mouseMoved(MouseEvent event) {
            //Jeśli włączony jest tryb rysowania wykonywane 
            //jest rysowanie specyficzne dla kazdej figury
            if(drawingFlag) {
                Point p;
                switch(menu.Flag){
                    case "Prostokat":
                        myRectangle r =(myRectangle) figureList.get(figureList.size() - 1);
                        p = new Point((int)r.getX(),(int) r.getY());
                        r.setFrameFromDiagonal(p, event.getPoint());
                        break;
                    case "Kolo":
                        myCircle c =(myCircle) figureList.get(figureList.size() - 1);
                        p = new Point((int)c.getX(),(int) c.getY());
                        c.setFrameFromDiagonal(p, event.getPoint());
                        c.width = Math.max(c.width, c.height);
                        c.height = c.width;
                        break;
                    case "Trojkat":
                        myTriangle t = (myTriangle) figureList.get(figureList.size() - 1);
                        t.changePoint(event.getPoint(), t.npoints-1);

                        //Ustawienie rysowania linii
                        if(t.npoints ==2)   {t.lineFlag = true;}
                        else                {t.lineFlag = false;}
                        break;
                        
                }
            }
            repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e){
            //Jeśli wybrana jest aktywna figura
            //zmieniany jest jej rozmiar
            if(activeShape!=null)
            {   
                if(activeShape instanceof myRectangle)
                {
                    if(e.getWheelRotation() > 0)
                    {
                        ((myRectangle)activeShape).resize(1.05*e.getScrollAmount());
                    }
                    else if(e.getWheelRotation() < 0)
                    {
                        ((myRectangle)activeShape).resize(0.95*e.getScrollAmount());
                    }
                }
                else if(activeShape instanceof myCircle)
                {
                    if(e.getWheelRotation() > 0)
                    {((myCircle)activeShape).resize(1.05*e.getScrollAmount());}
                    else if(e.getWheelRotation() < 0)
                    {
                        ((myCircle)activeShape).resize(0.95*e.getScrollAmount());
                    }
                    
                }
                else if(activeShape instanceof myTriangle)
                {
                    if(e.getWheelRotation() > 0)
                    {((myTriangle)activeShape).resize(1.05*e.getScrollAmount());}
                    else if(e.getWheelRotation() < 0)
                    {
                        ((myTriangle)activeShape).resize(0.95*e.getScrollAmount());
                    }
                    
                }
                repaint();
            }
        }
        
    }

    //Rozszerzona klasa prostokąta o potrzebne metody
    class myRectangle extends Rectangle2D.Double {
        myRectangle(){
            super();
        }

        myRectangle(double x, double y, double wight, double height){
            super(x,y,wight, height);
        }

        private boolean includeTest(Point2D p){
            return getBounds2D().contains(p);
        }

        public void move(Point2D p){
            this.x = p.getX();
            this.y = p.getY();
        }

        public void resize(double scale){
            this.height = this.height * scale;
            this.width = this.width * scale;
        }

        //Metoda wykorzystywana do zapisu do pliku
        public String covertToString(){
            return "myRectangle" + "$"+ String.valueOf(this.x) + "$" + String.valueOf(this.y)+ "$"+ String.valueOf(this.width)+ "$" +String.valueOf(this.height) +"$";
        }
    }

    //Rozszezona klasa elipsy wykorzystywana do kół
    class myCircle extends Ellipse2D.Double{
        myCircle(){
            super();
        }

        myCircle(double x, double y, double wight, double height){
            super(x,y,wight, height);
        }

        private boolean includeTest(Point2D p){
            return getBounds2D().contains(p);
        }

        public void move(Point2D p){
            this.x = p.getX();
            this.y = p.getY();
        }

        public void resize(double scale){
            this.height = this.height * scale;
            this.width = this.width * scale;
        }

        public String covertToString(){
            return "myCircle" + "$"+ String.valueOf(this.x) + "$"+ String.valueOf(this.y)+ "$"+ String.valueOf(this.width)+ "$" +String.valueOf(this.height) +"$";
        }
    }

    //Klasa rozszezająca klase polygon wykorzystywana do trójkątów
    //warto odnotować minus tego rozwiązania, poniewaz klasa polygon uzywa
    //współrzędnych całkowitych mogą się pojawić zniekształcenia (czasem duze)
    //przy skalowaniu
    class myTriangle extends Polygon{
        public boolean lineFlag;

        myTriangle(){
            super();
        }

        myTriangle(int[] xpoints, int[] ypoints, int npoints){
            super(xpoints, ypoints, npoints);
        }

        public void changePoint(Point2D p,int index){
            Point[] pTab = new Point[index];
            for(int i=0; i<index; i++)
            {
                pTab[i] = new Point(xpoints[i], ypoints[i]);
            }
            this.reset();
            for(int i=0; i<index; i++)
            {
                this.addPoint(pTab[i].x, pTab[i].y);
            }
            this.addPoint((int)p.getX(),(int) p.getY());
        }

        public void resize(double scale){
            //Metoda jest wykonywana w bloku try poniewaz operuje na
            //tablicach które nie mają początkowego rozmiaru 3
            try {
                //Deklaracja i ustawianie wersorów przesunięcia
                //aby skalowanie wykonywać w odpowiednim kierunku
                Point2D.Double vers1 = new Point2D.Double(0,0);
                Point2D.Double vers2 = new Point2D.Double(0,0);

                vers1.x = (this.xpoints[1] - this.xpoints[0]);
                vers1.y = (this.ypoints[1] - this.ypoints[0]);

                vers2.x =   (this.xpoints[2] - this.xpoints[0]);
                vers2.y =  (this.ypoints[2] - this.ypoints[0]);

                Point2D.Double new1 = new Point2D.Double(0,0);
                Point2D.Double new2 = new Point2D.Double(0,0);

                //Ustawianie nowych współrzędnych
                new1.x =this.xpoints[0] + scale*vers1.getX();
                new1.y =this.ypoints[0] + scale*vers1.getY();

                new2.x =this.xpoints[0] + scale*vers2.getX();
                new2.y =this.ypoints[0] + scale*vers2.getY();

                if(new1.x - (int)new1.x >=0.5) {new1.x= new1.x + 1;}
                if(new1.y - (int)new1.y >=0.5) {new1.y= new1.y + 1;}
                if(new2.x - (int)new2.x >=0.5) {new2.x= new2.x + 1;}
                if(new2.y - (int)new2.y >=0.5) {new2.y= new2.y + 1;}

                this.changePoint((Point2D) new1, 1);
                this.changePoint((Point2D) new2, 2);
            } catch (Exception e) {
        
        }
        }

        public String covertToString(){
            if(npoints == 3)
            {
                return "myTriangle" + "$"+ String.valueOf(this.xpoints[0]) + "$" + String.valueOf(this.xpoints[1])+ "$"+ String.valueOf(this.xpoints[2])+ "$" +String.valueOf(this.ypoints[0]) +"$" + String.valueOf(this.ypoints[1]) + "$" + String.valueOf(this.ypoints[2]) + "$";
            }
            else return "";
            
        }
    }
    
    //Metoda wczytująca grafikę z pliku .txt w którym
    //w kazdej linii zapisane są w odpowiedni sposób
    //figury, zakończonego linią $end$
    public void Load(File f){
        boolean finishFlag = false;
        String data;
        try {
            Scanner in = new Scanner(f);
            while(!finishFlag)
            {
                data = in.nextLine();
                if(data.equals("$end$")) {finishFlag=true;}
                else
                {
                    try {
                        addStringFigure(data);
                    } catch (Exception e) {
                        erFlag = true;
                    }
                    
                }
            }
            
        } catch (Exception e) {
            figureList.clear();
        }
    }

    //Metoda zapisująca figury do pliku
    public void Save(File f){           
        try {
            PrintWriter writer = new PrintWriter(f);

            for(int i=0; i<figureList.size(); i++)
            {
                String color = colorList.get(i).toString();
                color = color + "$";
                if(figureList.get(i) instanceof myRectangle) 
                {
                    writer.println(((myRectangle)figureList.get(i)).covertToString() + color);
                }
                else if(figureList.get(i) instanceof myCircle) {writer.println(((myCircle)figureList.get(i)).covertToString() + color);}
                else if(figureList.get(i) instanceof myTriangle)
                {
                    writer.println(((myTriangle)figureList.get(i)).covertToString() + color);
                }
                
            }
            writer.println("$end$");
            writer.close();
        } catch (Exception e) {
            
        }
        
    }

    //Metoda zamieniająca zapis w pliku na figurę
    private void addStringFigure(String figure) throws Exception{
        try {
            int i=0;
            String[] color = {"","",""};
            Shape f;
            String type = "";

            //Odczytywanie typu figury
            while(figure.charAt(i) != '$')
            {
                type =type + (figure.charAt(i) +"");
                i++;
            }
            i++;
            
            //Odczytywanie parametrów figury w zalezności od odczytanego typu
            switch(type){
                case "myRectangle":
                    String[] cords = {"", "", "", ""};
                    
                    for(int j=0; j<4; j++)
                    {
                        while(figure.charAt(i) != '$')
                        {
                            cords[j] = cords[j] + (figure.charAt(i) + "");
                            
                            i++;
                        }
                        i++;
                    }
                    f = new myRectangle(Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]), Double.parseDouble(cords[3]));
                    figureList.add(f);
                    break;
                case "myCircle":
                    String[] cord = {"", "", "", ""};
                    
                    for(int j=0; j<4; j++)
                    {
                        while(figure.charAt(i) != '$')
                        {
                            cord[j] = cord[j] + (figure.charAt(i) + "");
                            i++;
                        }
                        i++;
                    }
                    f = new myCircle(Double.parseDouble(cord[0]), Double.parseDouble(cord[1]), Double.parseDouble(cord[2]), Double.parseDouble(cord[3]));
                    figureList.add(f);
                 
                    break;
                case "myTriangle":
                    int[] xpoints =new int[3];
                    int[] ypoints = new int[3];

                    for(int j=0; j<3; j++)
                    {   
                        String xp = "";
                        while(figure.charAt(i) != '$')
                        {
                            xp= xp + (figure.charAt(i)+"");
                            i++;
                        }
                        xpoints[j] = Integer.parseInt(xp);
                        i++;
                    }
                    for(int j=0; j<3; j++)
                    {
                        String yp = "";
                        while(figure.charAt(i) != '$')
                        {
                            yp = yp + (figure.charAt(i) + "");
                            i++;
                        }
                        ypoints[j] = Integer.parseInt(yp);
                        i++;
                    }
                    f = new myTriangle(xpoints, ypoints, 3);
                    figureList.add(f);
                    break;
                default:
                    throw new Exception();
            }
            while(figure.charAt(i) != '[') {i++;}
            
            //Odczytanie koloru
            for(int j=0; j<3;j++)
            {   
                i++;
                i++;
                i++;
                while((figure.charAt(i) != ',') && (figure.charAt(i) != ']'))
                {
                    color[j] = color[j] + (figure.charAt(i) + "");
                    i++;
                }
            }
            colorList.add(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2]) ));
            repaint();
        } catch (Exception e) {
            throw new Exception();
        } 
    }
}

class myWindowAdapter extends WindowAdapter{
    public void windowClosing(WindowEvent e) {System.exit(0);}
}

//Rozszezona klasa okna o potrzebne metody
class myFrame extends JFrame implements ActionListener{
    public myPanel panel;
    private JTextField saveField, loadField;
    private MenuBar menuBar;
    private MenuItem info, instruction, save, load, importt;
    private Menu menu;
    private JDialog infoDialog, instructionDialog,saveDialog, loadDialog, errorDialog;
    private File file;

    myFrame(){
        super("KubiPaint");
        setBounds(200, 200, 500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Tworzenie menuBara i jego elementów
        menuBar = new MenuBar();
        menu = new Menu("Menu");
        info = new MenuItem("Info");
        instruction = new MenuItem("Instruction");
        
        menu.add(info);
        menu.add(instruction);
        menu.add(new MenuItem("Save"));
        menu.add(new MenuItem("Load"));
        
        menu.addActionListener(this);
        menuBar.add(menu);

        setMenuBar(menuBar);
        this.panel = new myPanel();

        //Tworzenie okna dialogowego z informacjami o programie
        infoDialog = new JDialog(this, "Info", true);
        infoDialog.setSize(370, 120);
        infoDialog.setLayout(new BorderLayout());
        infoDialog.setLocationRelativeTo(null);
        infoDialog.setResizable(false);
        
        JLabel L = new JLabel("KubiPaint");
        L.setHorizontalAlignment(SwingConstants.CENTER);
        infoDialog.add(L, BorderLayout.PAGE_START);
        JLabel L2 = new JLabel("<html>Prosty edytor graficzny w którym mozna tworzyć proste<br> grafiki zbudowane z figur: Koło, Prostokąt oraz Trójkąt</html>");
        L2.setHorizontalAlignment(SwingConstants.CENTER);
        infoDialog.add(L2, BorderLayout.CENTER);
        JLabel L3 = new JLabel("Autor: Mateusz Bystroński");
        L3.setHorizontalAlignment(SwingConstants.CENTER);
        infoDialog.add(L3, BorderLayout.PAGE_END);

        infoDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
   
        //Tworzenie okna dialogowego z instrukcją obsługi programu
        String instructionText =
        "INSTRUKCJA OBSŁUGI\r\n\r\n"+
        "1. Aby narysować figurę nalezy wybrac jedną z dostępnych na górnym\r\n    panelu figur, następnie korzystając z myszy ustalic jej granice. Aby\r\n    przerwać rysowanie figury nalezy nacisnac prawy przycisk myszy.\r\n"+
        "2. Kliknięcie na figurę oznacza ją jako aktywną.\r\n"+
        "3. Aby zmienic polozenie aktywnej figury nalezy kliknąć na nią lewym\r\n    przyciskiem myszy i przytrzymując przycisk zmienić jej połozenie.\r\n"+
        "4. Rozmiar aktywnej figury mozna zmienic ruszając scrollem, do góry\r\n    aby powiekszyc, w dół aby zmniejszyć.\r\n"+
        "5. Kliknięcie prawym przyciskiem myszy na aktywną figurę spowoduje\r\n    rozwinięcie menu figury, mozna w nim zmienić kolor figury oraz ją\r\n    usunąć";
        JTextArea instructionArea = new JTextArea(instructionText);
        instructionArea.setEditable(false);
        instructionDialog = new JDialog(this, "Instruction", false);
        instructionDialog.setSize(445, 230);
        instructionDialog.setResizable(false);
        instructionDialog.setLocationRelativeTo(null);
        instructionDialog.setLayout(new BorderLayout()); 
        instructionDialog.add(instructionArea, BorderLayout.CENTER);

        instructionDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        
        //Tworzenie okna dialogowego do obsługi zapisywania pliku
        saveDialog = new JDialog(this, "Save", true);
        saveDialog.setSize(370, 90);
        saveDialog.setLayout(new BorderLayout());
        saveDialog.setLocationRelativeTo(this);
        saveDialog.setResizable(false);

        JLabel label = new JLabel("Podaj docelową ściezkę pliku");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        saveDialog.add(label, BorderLayout.PAGE_START);

        saveField = new JTextField();
        saveField.setSize(350, 50);
        saveDialog.add(saveField, BorderLayout.CENTER);

        JButton b = new JButton("Zapisz");
        b.addActionListener(this);

        saveDialog.add(b, BorderLayout.PAGE_END);
        saveDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);

        //Tworzenie okna dialogowego do obsługi wczytywania pliku
        loadDialog = new JDialog(this, "Load", true);
        loadDialog.setSize(370, 90);
        loadDialog.setLayout(new BorderLayout());
        loadDialog.setLocationRelativeTo(this);
        loadDialog.setResizable(false);

        JLabel label2 = new JLabel("Podaj ściezkę pliku");
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        loadDialog.add(label2, BorderLayout.PAGE_START);

        loadField = new JTextField();
        loadField.setSize(350, 50);
        loadDialog.add(loadField, BorderLayout.CENTER);
        

        JButton b2 = new JButton("Wczytaj");
        b2.addActionListener(this);

        loadDialog.add(b2, BorderLayout.PAGE_END);
        loadDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);

        //Tworzenie okna dialogowego sygnalizującego błąd przy wyborze pliku
        errorDialog = new JDialog(this, "Error", true);
        errorDialog.setSize(370, 90);
        errorDialog.setLayout(new BorderLayout());
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setResizable(false);

        JLabel errorLabel = new JLabel("Nieprawidłowy format pliku !");
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorDialog.add(errorLabel, BorderLayout.CENTER);

        JButton errorButton = new JButton("Spróbuj ponownie");
        errorButton.addActionListener(this);
        errorDialog.add(errorButton, BorderLayout.PAGE_END);
        

        setLayout(new BorderLayout() );
        this.add(panel, BorderLayout.CENTER) ;
        
        addWindowListener(new myWindowAdapter());
    }
    //Klasa obsługująca akcje w menuBarze 
    //myFrame implementuje actionListener
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "Info":
                infoDialog.show();
                break;
            case "Instruction":
                instructionDialog.show();
                break;
            case "Save":
                if(file!=null)   {panel.Save(file);}
                else                {saveDialog.show();}
                break;
            case "Zapisz":              
                String path = saveField.getText();
                saveField.setText("");
                String format = "";
                
                if(path=="") 
                {
                    errorDialog.show();
                    break;
                }
                //Sprawdzanie formatu
                try {
                    for(int i = path.length()-4; i<path.length(); i++) {format = format + (path.charAt(i) + "");}
                } catch (Exception ex) {
                    errorDialog.show();
                    break;
                }
                switch(format){
                    case ".txt":
                        try {
                            file = new File(path);
                            if(file.exists()){}
                            else {file.createNewFile();}
                            panel.Save(file);
                        } catch (Exception ex) {
                            errorDialog.show();
                        }
                        saveDialog.hide();
                        break;
                    default:
                        errorDialog.show();
                        break;
                }    
                break;
            case "Load":
                if(file!=null) {}
                else {loadDialog.show();}
                break;
            case "Wczytaj":
                String path2 = loadField.getText();
                loadField.setText("");
                String format2 = "";
        
                if(path2=="") 
                {
                    errorDialog.show();
                    break;
                }
                //Sprawdzanie formatu
                try {
                    for(int i = path2.length()-4; i<path2.length(); i++) {format2 = format2 + (path2.charAt(i) + "");}
                } catch (Exception ex) {
                    errorDialog.show();
                    break;
                }
                switch(format2){
                    case ".txt":
                        file = new File(path2);
                        if(file.exists()) 
                        {
                            panel.Load(file);
                            if(panel.erFlag)
                            {
                                errorDialog.show();
                            }    
                            loadDialog.hide();
                        }
                        else 
                        {
                            file = null;
                            errorDialog.show();
                        }
                        break;
                    default:
                        errorDialog.show();
                }
                break;
            case "Spróbuj ponownie":
                errorDialog.hide();
                panel.erFlag = false;
                break;
        }
    }
}

//Klasa wykorzystana do budowy panelu z menu figur
class menuPanel extends JPanel{
    public String Flag;

    menuPanel(myPanel P){
        myButton Rectangle = new myButton(P, new ImageIcon("shape_rectangle.png"), "Prostokat");
        myButton Circle = new myButton(P, new ImageIcon("shape_circle.png"), "Kolo");
        myButton Triangle = new myButton(P, new ImageIcon("shape_triangle.png"), "Trojkat");
        this.add(Rectangle);
        this.add(Circle);
        this.add(Triangle);
    }
    
    //Klasa implementująca słuchacz zdarzeń do
    //przycisków panelu z menu figur
    class myButtonAdapter implements ActionListener{
        private myPanel P;
        String type;
        myButtonAdapter(myPanel P, String type) {this.P=P; this.type=type;}
        public void actionPerformed(ActionEvent e) {
            P.menu.Flag = type;
            P.activeShape = null;
            P.repaint();
        }
    }
    
    //Klasa przycisków panelu z menu figur
    class myButton extends JButton{
        myButton(myPanel P, Icon icon, String t)
        {
            super(icon);

            addActionListener(new myButtonAdapter(P, t));

        }
    }
}


