package game.Map;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import graphic.Drawable;
import util.Prop;

import java.util.ArrayList;
import java.util.List;

public class Chunk implements Drawable {
    private int size;
    private int posX;
    private int posY;
    public Tile[][] tiles;

    public Chunk(int x, int y, Tile[][] tileSet) {
        size = Integer.parseInt(Prop.getProp("chunkSize"));
        if (tileSet.length != size) {
            System.out.println("Не совпадают размеры чанок");
        }
        posX = x;
        posY = y;
        tiles = new Tile[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(tileSet[i], 0, tiles[i], 0, size);
            int j = 0;
            for (Tile tile : tiles[i]) {
                tile.setParentChunk(this);
                tile.setPosition(i,j);
                j++;
            }
        }
    }

    public List getNeighbors(int x, int y) {
        ArrayList<Tile> neighbors = new ArrayList<>();
        if (x != 0) {
            neighbors.add(tiles[x - 1][y]);
        }
        if (y != 0) {
            neighbors.add(tiles[x][y - 1]);
        }
        if (x != size - 1) {
            neighbors.add(tiles[x + 1][y]);
        }
        if (y != size - 1) {
            neighbors.add(tiles[x][y + 1]);
        }
        return neighbors;
    }

    @Override
    public void Draw(GL2 gl) {
        gl.glBegin(GL.GL_TRIANGLES);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                float h = tiles[j][i].getHeight();

                switch (tiles[j][i].getType()) {
                    case 0:
                        gl.glColor3f((h + 64) / 128, (h + 64) / 128, 0f);
                        break;
                    case 1:
                        gl.glColor3f(h / 128, h / 128, h / 128);
                        break;
                    case 2:
                        gl.glColor3f(0f, h / 128, 0f);
                        break;
                    case 3:
                        gl.glColor3f(0f, 0f, (h + 64) / 128);
                        break;
                }

                gl.glTexCoord2f(0.0625f, 1);
                gl.glVertex2i(posY * size + i, posX * size + j);

                gl.glTexCoord2f(0.0625f, 1 - 0.0625f);
                gl.glVertex2i(posY * size + i, posX * size + j - 1);

                gl.glTexCoord2f(0, 1 - 0.0625f);
                gl.glVertex2i(posY * size + i - 1, posX * size + j - 1);


                gl.glTexCoord2f(0.0625f, 1);
                gl.glVertex2i(posY * size + i, posX * size + j);

                gl.glTexCoord2f(0, 1);
                gl.glVertex2i(posY * size + i - 1, posX * size + j);

                gl.glTexCoord2f(0, 1 - 0.0625f);
                gl.glVertex2i(posY * size + i - 1, posX * size + j - 1);
            }
        }
        gl.glEnd();
    }
}
