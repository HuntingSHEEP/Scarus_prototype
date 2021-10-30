public class Matrix {



    public static double[][] rotationMatrix(Vector3D rotationAxis, double fi) {
        double[][] M = new double[3][3];

        //pierwszy wiersz
        M[0][0] = rotationAxis.x*rotationAxis.x* (1 - Math.cos(fi)) + Math.cos(fi);
        M[0][1] = rotationAxis.x*rotationAxis.y* (1 - Math.cos(fi)) - rotationAxis.z * Math.sin(fi);
        M[0][2] = rotationAxis.x*rotationAxis.z* (1 - Math.cos(fi)) + rotationAxis.y * Math.sin(fi);

        //drugi wiersz
        M[1][0] = rotationAxis.x*rotationAxis.y* (1 - Math.cos(fi)) + rotationAxis.z * Math.sin(fi);
        M[1][1] = rotationAxis.y*rotationAxis.y* (1 - Math.cos(fi)) + Math.cos(fi);
        M[1][2] = rotationAxis.y*rotationAxis.z* (1 - Math.cos(fi)) - rotationAxis.x * Math.sin(fi);

        //trzeci wiersz
        M[2][0] = rotationAxis.x*rotationAxis.z* (1 - Math.cos(fi)) - rotationAxis.y * Math.sin(fi);
        M[2][1] = rotationAxis.y*rotationAxis.z* (1 - Math.cos(fi)) + rotationAxis.x * Math.sin(fi);
        M[2][2] = rotationAxis.z*rotationAxis.z* (1 - Math.cos(fi)) + Math.cos(fi);;

        return M;
    }

    public static Vector3D multiply(double[][] M, Vector3D Point) {
        Vector3D P = new Vector3D(Point.x , Point.y, Point.z);
        double[] movedPoint = new double[3];

        for(int q=0; q<3; q++){
            double skladnik = 0;

            for(int j=0; j<3; j++){
                skladnik += M[q][j] * P.get(j);
            }

            movedPoint[q] = skladnik;
        }


        return new Vector3D(movedPoint[0] - P.x, movedPoint[1] - P.y, movedPoint[2] - P.z);
    }
}
