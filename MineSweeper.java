import com.badlogic.gdx.ApplicationAdapter; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.utils.TimeUtils;

public class MineSweeper extends ApplicationAdapter 
{
    private OrthographicCamera camera; //the camera to our world
    private Viewport viewport; //maintains the ratios of your world

    //These are all needed to draw text on the screeeeeeen!!!!!
    private SpriteBatch batch; 
    private BitmapFont font; 
    private BitmapFont fontArial20;
    private GlyphLayout layout; 
    private ShapeRenderer renderer;
    private TextureAtlas titleAppearAtlas;
    private TextureAtlas titleShineAtlas;
    private Animation<TextureRegion> animation1;
    private Animation<TextureRegion> animation2;
    private Texture background;
    private Texture backgroundWin;
    private Rectangle startButton;
    private Rectangle instructionsButton;
    private Rectangle helpButton;
    private Texture start;
    private Texture startPressed;
    private Texture help;
    private Texture helpPressed;
    private Rectangle deadButton;
    private Texture dead;
    private Texture deadPressed;
    private Rectangle smileButton;
    private Texture smile;
    private Texture smilePressed;
    private Rectangle newGameButton;
    private Texture newGame;
    private Texture newGamePressed;
    private Texture demo;
    private Texture details;
    private Rectangle settingsButton;
    private Texture settings;
    private Texture settingsPressed;
    private Texture backgroundSettings;
    private Rectangle backToGameButton;
    private Texture backToGame;
    private Texture backToGamePressed;
    private Rectangle minesButton;
    private Texture minesTexture;
    private Texture minesTexturePressed;

    private int[][] visualBoard;
    private int[][] board;
    private Rectangle[][] rectangleBoard;
    private ArrayList<Texture> images;

    private long startTime;
    private int seconds;
    private String timerDisplay;
    private int mines;
    private boolean unclicked;
    private float timePassed;
    private int timer;
    private int gap = Constants.WORLD_HEIGHT - Constants.WORLD_WIDTH;
    private boolean isSettings;

    private Gamestate gamestate;
    private Music gameMusic;
    @Override//this is called once when you first run your program
    public void create(){       
        camera = new OrthographicCamera(); 
        viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera); 

        batch = new SpriteBatch(); 
        layout = new GlyphLayout(); 
        font = new BitmapFont(Gdx.files.internal("Minesweeper Font.fnt"),
            Gdx.files.internal("Minesweeper Font.png"), false);
        font.getData().setScale(Constants.FONT_SCALE);
        fontArial20 = new BitmapFont(Gdx.files.internal("redtext.fnt"));
        // fontArial20.getData().setScale(Constants.FONT_SCALE);
        renderer = new ShapeRenderer();
        titleAppearAtlas = new TextureAtlas(Gdx.files.internal("Minesweeper Title Appear Atlas.atlas"));
        titleShineAtlas = new TextureAtlas(Gdx.files.internal("Minesweeper Title Shine Atlas.atlas"));
        animation1 = new Animation<TextureRegion>(1/12f, titleAppearAtlas.getRegions());
        animation2 = new Animation<TextureRegion>(1/8f, titleShineAtlas.getRegions());
        background = new Texture(Gdx.files.internal("background.png"));
        backgroundWin = new Texture(Gdx.files.internal("backgroundWin.png"));
        backgroundSettings = new Texture(Gdx.files.internal("settings.png"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMusic.wav"));
        gameMusic.setLooping(true);

        start = new Texture(Gdx.files.internal("start_unpressed.png"));
        startPressed = new Texture(Gdx.files.internal("start_pressed.png"));
        help = new Texture(Gdx.files.internal("help_unpressed.png"));
        helpPressed = new Texture(Gdx.files.internal("help_pressed.png"));
        dead = new Texture(Gdx.files.internal("deadButton.png"));
        deadPressed = new Texture(Gdx.files.internal("deadButtonPressed.png"));
        smile = new Texture(Gdx.files.internal("smileButton.png"));
        smilePressed = new Texture(Gdx.files.internal("smileButtonPressed.png"));
        newGame = new Texture(Gdx.files.internal("newGame.png"));
        newGamePressed = new Texture(Gdx.files.internal("newGamePressed.png"));
        demo = new Texture(Gdx.files.internal("demo.png"));
        details = new Texture(Gdx.files.internal("details.png"));
        settings = new Texture(Gdx.files.internal("settingsButton.png"));
        settingsPressed = new Texture(Gdx.files.internal("settingsButtonPressed.png"));
        backToGame = new Texture(Gdx.files.internal("backToGame.png"));
        backToGamePressed = new Texture(Gdx.files.internal("backToGamePressed.png"));
        minesTexture = new Texture(Gdx.files.internal("mines.png"));
        minesTexturePressed = new Texture(Gdx.files.internal("minesPressed.png"));

        gap = Constants.WORLD_HEIGHT - Constants.WORLD_WIDTH;
        startButton = new Rectangle(Constants.WORLD_WIDTH / 2 - 134 / 2 - Constants.WORLD_WIDTH / 4,
            Constants.WORLD_HEIGHT / 2 - 80, 134, 44);
        helpButton = new Rectangle(Constants.WORLD_WIDTH / 2 - 108 / 2 + Constants.WORLD_WIDTH / 4,
            Constants.WORLD_HEIGHT / 2 - 80, 108, 44);
        deadButton = new Rectangle(Constants.CENTER_X - gap/2, Constants.WORLD_WIDTH, gap, gap);
        smileButton = new Rectangle(Constants.CENTER_X - gap/2, Constants.WORLD_WIDTH, gap, gap);
        newGameButton = new Rectangle(150, 135, 100, 25);
        settingsButton = new Rectangle(Constants.WORLD_WIDTH - 83, 405, 75, 20);
        backToGameButton = new Rectangle(125, 150, 160, 40);
        minesButton = new Rectangle(125, 220, 160, 40);

        mines = 5;
        seconds = 0;
        timerDisplay = ""+seconds;
        unclicked = true;
        timePassed = 0;
        timer = 0;

        visualBoard = new int[20][20];
        fillVisual();

        board = new int[20][20];
        rectangleBoard = makeRectangleTiles();
        //board is officially initialized after first click

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
        images.add(new Texture("bomb.png")); //9th index in images
        images.add(new Texture("FacingDown.png")); //10th index in images
        images.add(new Texture("flagged.png")); //11th index in images
        images.add(new Texture("smileButton.png")); //12th index in images
        images.add(new Texture("cool.png")); //13th index in images
        images.add(new Texture("deadButton.png")); //14th index in images
        images.add(new Texture("tileHighlighted.png")); //15th index in images
        debugPrint();
    }

    @Override//this is called 60 times a second
    public void render(){

        //these two lines wipe and reset the screen so when something action had happened
        //the screen won't have overlapping images
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();

        //Functionality
        if(gamestate == Gamestate.GAME)
        {
            gameMusic.play();
            if(Gdx.input.isKeyJustPressed(Keys.R))
            {
                reset();
                gamestate = Gamestate.GAME;
            }
            else if(checkLoser()) //has lost
            {
                timer++;
                uncoverAllMines();
            }
            else if(checkWinner()) //has won
            {

            }
            else if(!isSettings)//not won or lost
            {
                uncover();
                seconds = (int)(MathUtils.nanoToSec * (TimeUtils.nanoTime() - startTime));
            }
        }
        if(gamestate == Gamestate.MENU || gamestate == Gamestate.INSTRUCTIONS)
        {
            gameMusic.stop();
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
                startTime = TimeUtils.nanoTime(); //gets start time of game
            }
        }

        //Visual
        batch.setProjectionMatrix(viewport.getCamera().combined); 

        batch.begin();
        if(!isSettings)
        {
            timePassed += Gdx.graphics.getDeltaTime();
        }

        if(gamestate == Gamestate.MENU)
            drawMenu(); 
        else if(gamestate == Gamestate.INSTRUCTIONS)
            drawInstructions(); 
        else if(gamestate == Gamestate.GAME)
        {
            drawBoard();
            if(checkWinner())
            {
                drawWinScreen();
            }
            if(isSettings)
            {
                drawSettings();
            }
        }

        batch.end();

        if(gamestate == Gamestate.GAME)
        {
            drawStats();
        }
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
                visualBoard[r][c] = 10; //10 means covered tile
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
        if(Gdx.input.justTouched()) //detects click
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
            if(inBounds && Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) //flagging
            {
                if(visualBoard[r][c] == 11) //unflags
                {
                    visualBoard[r][c] = 10;
                    System.out.print("UNFLAG c:" + c + " UNFLAG r:" + r + "\n");
                } else if(visualBoard[r][c] == 10)//flags
                {
                    visualBoard[r][c] = 11;
                    System.out.print("FLAG c:" + c + " FLAG r:" + r + "\n");
                }
            }
            if(inBounds && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && (visualBoard[r][c] != 11) && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) //uncovering
            {
                if(unclicked)
                {
                    //Set starting vales of bounds around target row and column assuming             it's not a corner, then adjust for corners with if condition
                    int rStart = r - 1; int rEnd = r + 1;
                    int cStart = c - 1; int cEnd = c + 1;

                    //Adjust if a corner and bounds are out of board
                    while(rStart < 0)
                        rStart++;
                    while(rEnd > board.length -1)
                        rEnd--;
                    while(cStart < 0)
                        cStart++;
                    while(cEnd > board[r].length - 1)
                        cEnd--;

                    //loop through and replace with spaces where mines can't be spawned
                    for(int row = rStart; row <= rEnd; row++)
                    {
                        for(int col = cStart; col <= cEnd; col++)
                        {
                            board[row][col] = 99;
                        }
                    }

                    //Another unmineable on each side to make game better
                    if(cStart - 1 >= 0)
                    {
                        board[(rStart + rEnd) / 2][cStart - 1] = 99;
                    }
                    if(rStart - 1 >= 0)
                    {
                        board[rStart - 1][(cStart + cEnd) / 2] = 99;
                    }
                    if(cEnd + 1 < board[r].length)
                    {
                        board[(rStart + rEnd) / 2][cEnd + 1] = 99;
                    }
                    if(rEnd + 1 < board.length)
                    {
                        board[rEnd + 1][(cStart + cEnd) / 2] = 99;
                    }

                    debugPrint();
                    //fill the board and replace the unmineables to empty spaces
                    fillBoard();
                    for(int row = 0; row < board.length; row++)
                    {
                        for(int col = 0; col < board[r].length; col++)
                        {
                            if(board[r][c] == 99)
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
                System.out.print("UNCOVER c:" + c + " UNCOVER r:" + r + " VisBoard val: " + visualBoard[r][c] + " Board val: " + board[r][c] + "\n");
            }
            else if(inBounds && Gdx.input.isButtonPressed(Keys.LEFT) && Gdx.input.isButtonPressed(Keys.RIGHT))
            {
                twoClick(r, c);
            }
        }

    }

    private void uncoverAllMines()
    {
        for(int r = 0; r < board.length; r++)
        {
            for(int c = 0; c < board[r].length; c++)
            {
                if(board[r][c] == 9)
                {
                    visualBoard[r][c] = board[r][c];
                }
            }
        }
    }

    private boolean checkWinner()
    {
        for(int r = 0; r < visualBoard.length; r++)
        {
            for(int c = 0; c < visualBoard[r].length; c++)
            {
                if(board[r][c] != 9 && visualBoard[r][c] == 10)
                {
                    return false;
                } 
            }
        }
        return true;
    }

    private boolean checkLoser()
    {
        for(int r = 0; r < visualBoard.length; r++)
        {
            for(int c = 0; c < visualBoard[r].length; c++)
            {
                if(visualBoard[r][c] == 9)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private void drawWinScreen()
    {
        batch.draw(backgroundWin, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
    }

    private void drawSettings()
    {
        batch.draw(backgroundSettings, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        Vector2 clickLoc = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        //textSize 100
        //textColor #f0f0f0
        //800 x 250
        //lightness 50
        if(!minesButton.contains(clickLoc))
        {
            batch.draw(minesTexture, 
                minesButton.x, 
                minesButton.y, 
                minesButton.width, 
                minesButton.height);
        }      
        else
        {
            batch.draw(minesTexturePressed, 
                minesButton.x, 
                minesButton.y, 
                minesButton.width, 
                minesButton.height);
            if(Gdx.input.justTouched())
            {
                //change mines
            }
        }
        if(!backToGameButton.contains(clickLoc))
        {
            batch.draw(backToGame, 
                backToGameButton.x, 
                backToGameButton.y, 
                backToGameButton.width, 
                backToGameButton.height);
        }      
        else
        {
            batch.draw(backToGamePressed, 
                backToGameButton.x, 
                backToGameButton.y, 
                backToGameButton.width, 
                backToGameButton.height);
            if(Gdx.input.justTouched())
            {
                isSettings = false;
            }
        }
    }

    private void drawMenu()
    {
        Vector2 clickLoc = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

        //Code for text
        batch.draw(background, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        font.setColor(1f, 1f, 1f, 1f);
        layout.setText(font, "Welcome to Minesweeper\nI: Instructions, G: Start the game!");
        font.draw(batch,
            layout,
            Constants.WORLD_WIDTH / 2 - layout.width / 2,
            Constants.WORLD_HEIGHT / 2 + layout.height / 2);

        //Play two animations, one after the other    
        batch.draw(animation1.getKeyFrame(timePassed, false), 
            Constants.WORLD_WIDTH / 2 - layout.width / 2 - 24, 
            Constants.WORLD_HEIGHT / 2 + layout.height / 2 + 20);

        //if first animation finished, then play next one
        if(animation1.isAnimationFinished(timePassed))
        {
            batch.draw(animation2.getKeyFrame(timePassed - animation1.getAnimationDuration(), true), 
                Constants.WORLD_WIDTH / 2 - layout.width / 2 - 24, 
                Constants.WORLD_HEIGHT / 2 + layout.height / 2 + 20);
        }

        //Code for pressing start button
        if(!startButton.contains(clickLoc))
        {
            batch.draw(start, 
                startButton.x, 
                startButton.y, 
                startButton.width, 
                startButton.height);
        }      
        else
        {
            batch.draw(startPressed, 
                startButton.x, 
                startButton.y, 
                startButton.width, 
                startButton.height);
            if(Gdx.input.justTouched())
            {
                gamestate = gamestate.GAME;
                startTime = TimeUtils.nanoTime(); //gets start time of game
            }
        }

        //Help Button
        if(!helpButton.contains(clickLoc))
        {
            batch.draw(help, 
                helpButton.x, 
                helpButton.y, 
                helpButton.width, 
                helpButton.height);
        }      
        else
        {
            batch.draw(helpPressed, 
                helpButton.x, 
                helpButton.y, 
                helpButton.width, 
                helpButton.height);
            if(Gdx.input.justTouched())
            {
                gamestate = gamestate.INSTRUCTIONS; 
            }
        }
    }

    private void drawInstructions()
    {   
        font.setColor(1f, 0f, 0f, 1f);
        batch.draw(background, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        layout.setText(font, "Do NOT click the MINES");

        font.draw(batch,
            layout, 
            Constants.WORLD_WIDTH / 2 - layout.width / 2, 
            370);

        batch.draw(details,
            25, 280,
            350, 60);

        batch.draw(demo,
            62, 15,
            275, 250);
    }

    private void drawStats() //draws top bar, with smiley face, time and #of mines
    {
        int rect_w = Constants.WORLD_WIDTH / 6; //width of both #ofmines and timer
        int gap = Constants.WORLD_HEIGHT - Constants.WORLD_WIDTH; //height of the stats bar

        renderer.setProjectionMatrix(viewport.getCamera().combined);
        renderer.begin(ShapeType.Filled);
        renderer.setColor(Color.LIGHT_GRAY); // main gray rect
        if(checkWinner() || isSettings)
        {
            renderer.setColor(Color.DARK_GRAY);
        }
        renderer.rect(0, Constants.WORLD_WIDTH, Constants.WORLD_WIDTH, gap); 
        renderer.setColor(Color.BLACK); // timer rect
        renderer.rect(Constants.CENTER_X + gap,Constants.WORLD_WIDTH + 3,rect_w,gap-6); 
        renderer.end();

        batch.begin(); //draw time
        int xCordTime = Constants.CENTER_X + rect_w + gap/2;
        int yCordTime = Constants.WORLD_HEIGHT - 6;
        timerDisplay = ""+seconds;
        if(seconds > 599) //4 digits
        {
            xCordTime = Constants.CENTER_X + rect_w - 21; //i might hard code all these "gap" values
            timerDisplay = ""+(seconds / 60) + ":" + seconds%60;
            if(seconds%60 < 10)
            {
                timerDisplay = ""+(seconds / 60) + ":0" + seconds%60;
            }
        } else if(seconds > 59) //3 digits
        {
            xCordTime = Constants.CENTER_X + rect_w - gap/3;
            timerDisplay = ""+(seconds / 60) + ":" + seconds%60;
            if(seconds%60 < 10)
            {
                timerDisplay = ""+(seconds / 60) + ":0" + seconds%60;
            }
        } else if(seconds > 9) // one digit
        {
            xCordTime = Constants.CENTER_X + rect_w + gap/4;
        }
        fontArial20.draw(batch, timerDisplay, xCordTime,yCordTime);
        batch.end();

        renderer.begin(ShapeType.Filled); // mines rect
        renderer.rect(Constants.CENTER_X - rect_w - gap,Constants.WORLD_WIDTH + 3,rect_w,gap-6); 
        renderer.end();
        batch.begin();
        fontArial20.draw(batch, ""+mines, Constants.CENTER_X - gap - rect_w/2 - 6, Constants.WORLD_HEIGHT - 6); //mines

        Vector2 clickLoc = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY())); //both buttons
        if(checkLoser())
        {
            if(!deadButton.contains(clickLoc))
            {
                batch.draw(dead, 
                    deadButton.x, 
                    deadButton.y, 
                    deadButton.width, 
                    deadButton.height);
            }      
            else
            {
                batch.draw(deadPressed, 
                    deadButton.x, 
                    deadButton.y, 
                    deadButton.width, 
                    deadButton.height);
                if(Gdx.input.justTouched())
                {
                    reset();
                }
            }
        } else if(checkWinner()) {
            if(!newGameButton.contains(clickLoc))
            {
                batch.draw(newGame, 
                    newGameButton.x, 
                    newGameButton.y, 
                    newGameButton.width, 
                    newGameButton.height);
            }      
            else
            {
                batch.draw(newGamePressed, 
                    newGameButton.x, 
                    newGameButton.y, 
                    newGameButton.width, 
                    newGameButton.height);
                if(Gdx.input.justTouched())
                {
                    reset();
                }
            }
        } else if(!isSettings)
            if(!smileButton.contains(clickLoc))
            {
                batch.draw(smile, 
                    smileButton.x, 
                    smileButton.y, 
                    smileButton.width, 
                    smileButton.height);
            }      
            else
            {
                batch.draw(smilePressed, 
                    smileButton.x, 
                    smileButton.y, 
                    smileButton.width, 
                    smileButton.height);
                if(Gdx.input.justTouched())
                {
                    reset();
                }
            }
        if(!settingsButton.contains(clickLoc) && !isSettings)
        {
            batch.draw(settings, 
                settingsButton.x, 
                settingsButton.y, 
                settingsButton.width, 
                settingsButton.height);
        }      
        else if(!isSettings)
        {
            batch.draw(settingsPressed, 
                settingsButton.x, 
                settingsButton.y, 
                settingsButton.width, 
                settingsButton.height);
            if(Gdx.input.justTouched())
            {
                isSettings = true;
            }
        }
        batch.end();
    }

    private void drawBoard()
    {
        Vector2 clickLoc = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        int rowClick = ((int)clickLoc.y - Constants.WORLD_WIDTH) / (-1 * Constants.CELL_SIDE);
        int colClick = (int)clickLoc.x / Constants.CELL_SIDE;

        //Set starting vales of bounds around target row and column assuming             it's not a corner, then adjust for corners with if condition
        int rStart = rowClick - 1; int rEnd = rowClick + 1;
        int cStart = colClick - 1; int cEnd = colClick + 1;

        if(Gdx.input.justTouched())
        {
            //Adjust if a corner and bounds are out of board
            if(rStart < 0)
                rStart++;
            if(rEnd == board.length)
                rEnd--;
            if(cStart < 0)
                cStart++;
            if(rowClick >= 0 && !checkWinner() && !checkLoser() && cEnd == board[rowClick].length)
                cEnd--;
        }

        System.out.println("Row Start: " + rStart + " Col Start: " + cStart);
        System.out.println("Row End: " + rEnd + " Col End: " + cEnd);
        System.out.println("Row Click: " + rowClick + " Col Click " + colClick);

        for(int r = 0; r < visualBoard.length; r++)
        {
            for(int c = 0; c < visualBoard[0].length; c++)
            {

                int num = visualBoard[r][c];
                int x = c * Constants.CELL_SIDE;
                int y = Constants.WORLD_WIDTH - (r + 1) * Constants.CELL_SIDE;
                if(!checkWinner() && !checkLoser() && !isSettings && rectangleBoard[r][c].contains(clickLoc) && visualBoard[r][c] == 10)
                {
                    batch.draw(images.get(15), 
                        x,
                        y,
                        Constants.CELL_SIDE, 
                        Constants.CELL_SIDE);
                }     
                else
                {
                    batch.draw(images.get(num), 
                        x,
                        y,
                        Constants.CELL_SIDE, 
                        Constants.CELL_SIDE);
                }
                if(!checkWinner() && !checkLoser() && !isSettings && Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                {
                    for(int row = rStart; row <= rEnd; row++)
                    {
                        for(int col = cStart; col <= cEnd; col++)
                        {
                            batch.draw(images.get(15), 
                                col * Constants.CELL_SIDE,
                                Constants.WORLD_WIDTH - (row + 1) * Constants.CELL_SIDE,
                                Constants.CELL_SIDE, 
                                Constants.CELL_SIDE);
                        }
                    }
                }
            }
        }
    }

    private void generateMines() //Maybe we should name this generateMines() and
    //then name another method fillBoard where we 
    //generate the board (mines, neighbors,etc.)
    {
        int r, c;

        for(int i = 0; i < mines; i++) 
        {
            do
            {
                r = (int)(Math.random() * 20);
                c = (int)(Math.random() * 20);
            }
            while(board[r][c] != 0);

            board[r][c] = 9; //9 means uncovered mine
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

    private boolean twoClick(int row, int col)
    {
        boolean correctlyFlagged = true;

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

        //loop through and count type of neighbor
        for(int r = rStart; r <= rEnd; r++)
        {
            for(int c = cStart; c <= cEnd; c++)
            {
                if(board[r][c] == 9 && visualBoard[r][c] != 11 && visualBoard[row][col] < 9)
                {
                    correctlyFlagged = false;
                }
            }
        }

        if(correctlyFlagged && visualBoard[row][col] < 9)
        {
            for(int r = rStart; r <= rEnd; r++)
            {
                for(int c = cStart; c <= cEnd; c++)
                {
                    if(visualBoard[r][c] == 10)
                    {
                        chain(r, c);
                        visualBoard[r][c] = board[r][c];
                    }
                }
            }
        }

        return correctlyFlagged;
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
        startTime = TimeUtils.nanoTime(); //gets start time of game
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

    private Rectangle[][] makeRectangleTiles()
    {
        Rectangle[][] tiles = new Rectangle[20][20];
        for(int r = 0; r < visualBoard.length; r++)
        {
            for(int c = 0; c < visualBoard[0].length; c++)
            {
                int x = c * Constants.CELL_SIDE;
                int y = Constants.WORLD_WIDTH - (r + 1) * Constants.CELL_SIDE;
                Rectangle tile = new Rectangle(x, y, Constants.CELL_SIDE, Constants.CELL_SIDE);
                tiles[r][c] = tile;
            }
        }
        return tiles;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true); 
    }

    @Override
    public void dispose(){
        batch.dispose();
        titleAppearAtlas.dispose();
        titleShineAtlas.dispose();
    }
}
