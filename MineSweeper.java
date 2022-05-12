import com.badlogic.gdx.ApplicationAdapter; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.Input.Keys; 
import com.badlogic.gdx.math.Vector2; 
import com.badlogic.gdx.math.MathUtils; 
import com.badlogic.gdx.math.Intersector; 
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Texture; 
import com.badlogic.gdx.InputProcessor; 
import com.badlogic.gdx.*; 
import com.badlogic.gdx.utils.Array;  
import java.util.*; 

public class MineSweeper extends ApplicationAdapter 
{
    private OrthographicCamera camera; //the camera to our world
    private Viewport viewport; //maintains the ratios of your world

    //These are all needed to draw text on the screeeeeeen!!!!!
    private SpriteBatch batch; 
    private BitmapFont font; 
    private GlyphLayout layout; 
    private ShapeRenderer renderer; 

    private int[][] visualBoard;
    private int[][] board;
    private ArrayList<Texture> images;

    private int timer;
    private int mines;
    private boolean unclicked;

    private Gamestate gamestate; 

    @Override//this is called once when you first run your program
    public void create(){       
        camera = new OrthographicCamera(); 
        viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera); 

        batch = new SpriteBatch(); 
        layout = new GlyphLayout(); 
        font = new BitmapFont(); 
        renderer = new ShapeRenderer(); 

        mines = 50;
        timer = 0;
        unclicked = true;

        visualBoard = new int[20][20];
        fillVisual();

        board = new int[20][20];
        
        images = new ArrayList<Texture>();
        gamestate = Gamestate.MENU; 

        images.add(new Texture("0.png")); 
        images.add(new Texture("1.png")); 
        images.add(new Texture("2.png")); 
        images.add(new Texture("3.png")); 
        images.add(new Texture("4.png")); 
        images.add(new Texture("5.png")); 
        images.add(new Texture("6.png")); 
        images.add(new Texture("7.png")); 
        images.add(new Texture("8.png")); 
        images.add(new Texture("bomb.png")); //#9th index in images

        images.add(new Texture("FacingDown.png")); //#10th index in images
        debugPrint();
    }

    @Override//this is called 60 times a second
    public void render(){
        //these two lines wipe and reset the screen so when something action had happened
        //the screen won't have overlapping images
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(gamestate == Gamestate.GAME)
        {
            uncover();
            drawStats();
            if(Gdx.input.isKeyJustPressed(Keys.R))
            {
                reset(); 
            }
        }
        if(gamestate == Gamestate.MENU || gamestate == Gamestate.INSTRUCTIONS)
        {
            if(Gdx.input.isKeyJustPressed(Keys.I))
            {
                gamestate = Gamestate.INSTRUCTIONS;
            }
            if(Gdx.input.isKeyJustPressed(Keys.M))
            {
                gamestate = Gamestate.MENU;
            }
            if(Gdx.input.isKeyJustPressed(Keys.G))
            {
                gamestate = Gamestate.GAME; 
            }
        }

        batch.setProjectionMatrix(viewport.getCamera().combined); 
        batch.begin();

        if(gamestate == Gamestate.MENU)
            drawMenu(); 
        else if(gamestate == Gamestate.INSTRUCTIONS)
            drawInstructions(); 
        else if(gamestate == Gamestate.GAME)
        {
            drawBoard();
        }
        batch.end();
    }

    private void debugPrint()
    {
        for(int[] row : board)
        {
            for(int element : row)
            {
                System.out.print(element + "  "); 
            }
            System.out.println(); 
        }
        System.out.println(); 
    }

    private void fillVisual()
    {
        for(int r = 0; r<visualBoard.length; r++)
        {
            for(int c = 0; c<visualBoard[r].length; c++)
            {
                visualBoard[r][c] = 10; //#10 means covered tile
            }
        }
    }

    private void fillBoard() //Maybe we should name this generateMines() and
    //then name another method fillBoard where we 
    //generate the board (mines, neighbors,etc.)
    {
        generateMines();
        for(int r = 0; r < board.length; r++)
        {
            for(int c = 0; c < board[r].length; c++)
            {
                if(board[r][c] != 9)
                {
                    board[r][c] = countNeighbors(r, c);
                }
            }
        }
    }

    private void uncover()
    {
        if(Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        {
            boolean inBounds = true;

            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            Vector2 worldCoord = viewport.unproject(new Vector2(x,y));
            x = (int)worldCoord.x;
            y = (int)worldCoord.y;

            if(x < 0 || x > 400 || y < 0 || y > 400)
            {
                inBounds = false;
            }

            int c = x / Constants.CELL_SIDE;
            int r = 19 - y / Constants.CELL_SIDE;

            if(inBounds)
            {
                if(unclicked)
                {
                    //Set starting vales of bounds around target row and column assuming             it's not a corner, then adjust for corners with if condition
                    int rStart = r - 1; int rEnd = r + 1;
                    int cStart = c - 1; int cEnd = c + 1;

                    //Adjust if a corner and bounds are out of board
                    if(rStart < 0)
                        rStart++;
                    if(rEnd == board.length)
                        rEnd--;
                    if(cStart < 0)
                        cStart++;
                    if(cEnd == board[r].length)
                        cEnd--;

                    //loop through and replace with spaces where mines can't be spawned
                    for(int row = rStart; row <= rEnd; row++)
                    {
                        for(int col = cStart; col <= cEnd; col++)
                        {
                            board[row][col] = 11;
                        }
                    }
                    debugPrint();
                    
                    //fill the board and replace the unmineables to empty spaces
                    fillBoard();
                    for(int row = rStart; row <= rEnd; row++)
                    {
                        for(int col = cStart; col <= cEnd; col++)
                        {
                            if(board[r][c] == 11)
                            {
                                board[r][c] = 0;
                            }
                        }
                    }
                    
                    //chain if possible and then replace with visual
                    chain(r, c);
                    visualBoard[r][c] = board[r][c];
                    unclicked = false;
                }
                chain(r, c);
                visualBoard[r][c] = board[r][c];
            }

            System.out.print("UNCOVER c:" + c + " UNCOVER r:" + r + " VisBoard val: " + visualBoard[r][c] + " Board val: " + board[r][c] + "\n");
        }
    }

    private boolean checkWinner()
    {
        return false; 
    }

    private boolean checkLoser()
    {
        return false; 
    }

    private void drawMenu()
    {
        font.setColor(1f, 1f, 1f, 1f);
        layout.setText(font, "Welcome to Minesweeper\nI: Instructions, G: Start the game!");
        font.draw(batch,
            layout,
            Constants.WORLD_WIDTH / 2 - layout.width / 2,
            Constants.WORLD_HEIGHT / 2 + layout.height / 2);

    }

    private void drawInstructions()
    {   
        font.setColor(1f, 0f, 0f, 1f);
        layout.setText(font, "Right click to flag tiles.\nLeft click to reveal tiles, avoid mines.\nPress M to return to the menu\nPress G to start the game!");
        font.draw(batch,
            layout, 
            Constants.WORLD_WIDTH / 2 - layout.width / 2, 
            Constants.WORLD_HEIGHT / 2 + layout.height / 2);

    }

    private void drawStats() //#draws top bar, with smiley face, time and #of mines
    {
        int rect_w = 0;
        renderer.setProjectionMatrix(viewport.getCamera().combined);
        renderer.begin(ShapeType.Filled);
        renderer.setColor(Color.LIGHT_GRAY); 

        //Draws bar at top
        renderer.rect(0, Constants.WORLD_WIDTH,  
            Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT-Constants.WORLD_WIDTH); //# main gray rect

        renderer.rect(Constants.CENTER_X,0,0,0); //# mines rect
        renderer.end();
    }

    private void drawBoard()
    {
        for(int r = 0; r < visualBoard.length; r++)
        {
            for(int c = 0; c < visualBoard[0].length; c++)
            {

                int num = visualBoard[r][c];

                batch.draw(images.get(num), 
                    c*Constants.CELL_SIDE,
                    //Constants.WORLD_HEIGHT - (Constants.WORLD_HEIGHT-Constants.WORLD_WIDTH) -(r+1)*Constants.CELL_SIDE,
                    Constants.WORLD_WIDTH - (r + 1) * Constants.CELL_SIDE,
                    Constants.CELL_SIDE, 
                    Constants.CELL_SIDE);   
            }
        }

    }

    private void generateMines() //Maybe we should name this generateMines() and
    //then name another method fillBoard where we 
    //generate the board (mines, neighbors,etc.)
    {
        int r, c;

        for(int i = 0; i < mines; i++) //#random mines, used to test uncover()
        {
            do
            {
                r = (int)(Math.random() * 20);
                c = (int)(Math.random() * 20);
            }
            while(board[r][c] != 0);

            board[r][c] = 9; //#9 means uncovered mine
        }
    }

    private int countNeighbors(int row, int col)
    {
        int ctr = 0;

        //Set starting vales of bounds around target row and column assuming             it's not a corner, then adjust for corners with if condition
        int rStart = row - 1; int rEnd = row + 1;
        int cStart = col - 1; int cEnd = col + 1;

        //Adjust if a corner and bounds are out of board
        if(rStart < 0)
            rStart++;
        if(rEnd == board.length)
            rEnd--;
        if(cStart < 0)
            cStart++;
        if(cEnd == board[row].length)
            cEnd--;

        //loop through and count neighbors
        for(int r = rStart; r <= rEnd; r++)
        {
            for(int c = cStart; c <= cEnd; c++)
            {
                if(board[r][c] == 9)
                {
                    ctr++;
                }
            }
        }

        return ctr;
    }

    private void reset()
    {
        for(int r = 0; r < board.length; r++)
        {
            for(int c = 0; c < board[r].length; c++)
            {
                board[r][c] = 0;
                visualBoard[r][c] = 10;
            }
        }
        unclicked = true;
    }
    
    private void chain(int r, int c)
    {
        //Checks if in bounds and covered
        if(r == board.length || c == board[0].length || r < 0 || c < 0 || visualBoard[r][c] != 10){}
        //Checks if empty
        else if(board[r][c] == 9){}
        //If neighboring tile uncover and end
        else if(board[r][c] > 0 && board[r][c] < 9)
        {
            visualBoard[r][c] = board[r][c];
        }
        //Else it uncovers tile and chains behavior through recursion
        else
        {
            visualBoard[r][c] = board[r][c];
            chain(r-1, c-1); chain(r-1, c); chain(r-1, c+1); //chains for row above target
            chain(r, c-1); chain(r, c+1);                    //chains for sides adjacent to target on same row
            chain(r+1, c-1); chain(r+1, c); chain(r+1, c+1); //chains for row below target
        }     
    }
    
    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true); 
    }

    @Override
    public void dispose(){
        batch.dispose();

    }
}
