import javax.swing.JOptionPane;

public class Marco{
   public static void main(String[] args) {
      int filas = Integer.parseInt(JOptionPane.showInputDialog("Numero de filas"));
      int columnas = Integer.parseInt(JOptionPane.showInputDialog("Numero de columnas"));
      int matriz[][] = new int[filas][columnas];

      for(int i = 0; i<filas ;i++){
         for(int j  = 0; j<columnas ;j++){
            if(i == 0 || i == filas-1)
               matriz[i][j] = 1;
            else if(j == 0 || j == columnas-1)
               matriz[i][j] = 1;
            else
               matriz[i][j] = 0;
         }
      }
      for(int i = 0; i<matriz.length ;i++){
         for(int j  = 0; j<matriz[i].length ;j++)
            System.out.print(matriz[i][j]);
         System.out.println("");
      }
   }
}