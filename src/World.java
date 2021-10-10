import java.util.ArrayList;
import java.util.List;

public class World {
    class Chunk {
        private List<GameObject> lista = new ArrayList<GameObject>();

        public void append(GameObject GO){
            lista.add(GO);
        }

        public void remove(int index){
            lista.remove(index);
        }

    }


    Chunk[][] chunks;
    private int size;
    private int chunkPixelWidth;

    World(int size, int chunkPixelWidth){
        this.size = size;
        this.chunkPixelWidth = chunkPixelWidth;

        chunks= new Chunk[size*2][size*2];
    }

    public void add(GameObject GO){
        if(GO.type.compareTo(SRectangle.myType) == 0){
            Vector3D LU = ((SRectangle) GO).getLUpperVertex();
            Vector3D RU = ((SRectangle) GO).getRUpperVertex();
            Vector3D RL = ((SRectangle) GO).getRLowerVertex();
            Vector3D LL = ((SRectangle) GO).getLLowerVertex();

            Vector3D chLU = new Vector3D((int) (LU.y/chunkPixelWidth) + size, (int) (LU.x/chunkPixelWidth) + size);
            Vector3D chRU = new Vector3D((int) (RU.y/chunkPixelWidth) + size, (int) (RU.x/chunkPixelWidth) + size);
            Vector3D chRL = new Vector3D((int) (RL.y/chunkPixelWidth) + size, (int) (RL.x/chunkPixelWidth) + size);
            Vector3D chLL = new Vector3D((int) (LL.y/chunkPixelWidth) + size, (int) (LL.x/chunkPixelWidth) + size);

            //TODO: wybrać jedynie unikatowe chunki

            //chunks[(int) (GO.location.position.y/chunkPixelWidth)+size][(int) (GO.location.position.x/chunkPixelWidth)+size].append(GO);

        }else if(GO.type.compareTo(SCircle.myType) == 0){

        }

    }


}
