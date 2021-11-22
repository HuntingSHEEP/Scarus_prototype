import java.util.ArrayList;
import java.util.List;

public class World {
    private class Chunk {
        private List<GameObject> lista = new ArrayList<GameObject>();
        public void append(GameObject GO){
            lista.add(GO);
        }
        public void remove(int index){
            lista.remove(index);
        }
        public GameObject get(int index){return lista.get(index);
        }
    }


    Chunk[][] chunks;
    List<GameObject> gameObjectList = new ArrayList<GameObject>();

    private int size;
    private int chunkPixelWidth;
    public double physicFps = 0;

    World(int size, int chunkPixelWidth){
        this.size = size;
        this.chunkPixelWidth = chunkPixelWidth;

        chunks= new Chunk[size*2][size*2];

        for(int x=0; x<size*2; x++)
            for(int y=0; y<size*2; y++)
                chunks[x][y]=new Chunk();
    }

    public void registerInChunks(GameObject GO){
        if(GO.type.compareTo(SRectangle.myType) == 0){
            Vector3D LU = ((SRectangle) GO).getLUpperVertex();
            Vector3D RU = ((SRectangle) GO).getRUpperVertex();
            Vector3D RL = ((SRectangle) GO).getRLowerVertex();
            Vector3D LL = ((SRectangle) GO).getLLowerVertex();
            Vector3D mM = GO.location.position;

            Vector3D chLU = new Vector3D((int) (LU.y/chunkPixelWidth + size), (int) (LU.x/chunkPixelWidth + size));
            Vector3D chRU = new Vector3D((int) (RU.y/chunkPixelWidth + size), (int) (RU.x/chunkPixelWidth + size));
            Vector3D chRL = new Vector3D((int) (RL.y/chunkPixelWidth + size), (int) (RL.x/chunkPixelWidth + size));
            Vector3D chLL = new Vector3D((int) (LL.y/chunkPixelWidth + size), (int) (LL.x/chunkPixelWidth + size));
            Vector3D mChk = new Vector3D((int) (mM.y/chunkPixelWidth + size), (int) (mM.x/chunkPixelWidth + size));

            /*
            System.out.println(String.format("DATA: size [%d]  chunkPixelWidth [%d]", size, chunkPixelWidth));
            System.out.println(String.format(
                            "LU %s -> CHUNK %s\n" +
                            "RU %s -> CHUNK %s\n" +
                            "RL %s -> CHUNK %s\n" +
                            "LL %s -> CHUNK %s\n" +
                            "mM %s -> CHUNK %s", LU, chLU, RU, chRU, RL, chRL, LL,chLL, mM, mChk));

             */

            Vector3D[] chnkList = {chLU, chRU, chRL, chLL, mChk};


            for(int i=0; i<chnkList.length; i++)
                if(! chunks[chnkList[i].x.intValue()][chnkList[i].y.intValue()].lista.contains(GO)){
                    chunks[chnkList[i].x.intValue()][chnkList[i].y.intValue()].append(GO);
                    //System.out.println(String.format("Added %s to chunk [%d, %d]",GO,chnkList[i].x.intValue(),chnkList[i].y.intValue()));
                }

            //TODO: sprawdzanie, czy wierzchołek nie wykracza poza zakres świata
            //TODO: dynamiczne rozszerzanie świata


        }else if(GO.type.compareTo(SCircle.myType) == 0){
        }
    }

    public void deregisterFromChunks(GameObject gameObject){
        for(int x=0; x<size*2; x++)
            for(int y=0; y<size*2; y++)
                if (chunks[y][x].lista.contains(gameObject)){
                    chunks[y][x].lista.remove(gameObject);
                    //System.out.println(String.format("Deregistered %s from CHUNK [%d, %d]", gameObject, y, x));
                }
    }

    public void add(GameObject gameObject){
        registerInChunks(gameObject);
        gameObjectList.add(gameObject);
    }


}
