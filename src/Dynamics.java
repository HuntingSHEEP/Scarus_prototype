public class Dynamics {
    Vector3D v, a;
    Vector3D omega;

    //modyfikatory tymczasowe
    Vector3D tempA;

    Dynamics(){
        v = new Vector3D();
        a = new Vector3D();
        omega = new Vector3D();
        tempA = new Vector3D();
    }
}
