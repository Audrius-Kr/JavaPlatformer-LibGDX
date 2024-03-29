/* Audrius Kralikas

 */

package screens;

import Tools.B2WorldCreator;
import Tools.WorldListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.CatGame;

import screens.GameOverScreen;
import sprites.FinalCat;
import sprites.Player;

public class GameScreen implements Screen {

    private final int BCGRD_LAYER_INDEX = 0;
    public static final float GRAVITY_STRENGTH = -10f;
    private OrthographicCamera gameCamera;
    private Viewport gamePort;
    private CatGame game;
    private boolean onGround;
    TextureRegion idleFrame;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private B2WorldCreator creator;
    private OrthogonalTiledMapRenderer renderer;
    private World world;
    private Box2DDebugRenderer b2dr;
    private Player player;
    TiledMapImageLayer backgroundLayer;
    TextureRegion background;
    private float deadTimer = 0;
    int repeatX;
    public Music main;




    public GameScreen(CatGame game) {
        this.game = game;
        gameCamera = new OrthographicCamera();
        gamePort = new StretchViewport(CatGame.SCREEN_WIDTH / CatGame.PPM, CatGame.SCREEN_HEIGHT / CatGame.PPM, gameCamera);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("tiled/map1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / CatGame.PPM);
        gameCamera.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, GRAVITY_STRENGTH), true);
        b2dr = new Box2DDebugRenderer();
        player = new Player(world, 100, 100);
        creator = new B2WorldCreator(this);
        world.setContactListener(new WorldListener());
        backgroundLayer = (TiledMapImageLayer) map.getLayers().get(BCGRD_LAYER_INDEX);
        background = backgroundLayer.getTextureRegion();
        repeatX = (int) Math.ceil(Gdx.graphics.getWidth() / (background.getRegionWidth() / CatGame.PPM));
        main = Gdx.audio.newMusic(Gdx.files.internal("main.mp3"));
        main.setLooping(true);

    }


    @Override
    public void show() {

    }

    public void update(float dt) {
        main.setVolume(0.1f);
        main.play();


            world.step(1 / 60f, 6, 2);
            player.update(dt);
        player.handleInput(dt);
            for (FinalCat entity : creator.getFinalCats()) {
                entity.update(dt);

            }

            if(!player.dead) {

                gameCamera.position.x = player.b2body.getPosition().x;
                gameCamera.update();
                renderer.setView(gameCamera);
            }


    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gameCamera.combined);
        game.batch.begin();


        for (int x = 0; x < repeatX; x++) {
                game.batch.draw(background, x * background.getRegionWidth() / CatGame.PPM, 0, background.getRegionWidth() / CatGame.PPM, background.getRegionHeight() / CatGame.PPM);
        }

        for (FinalCat entity : creator.getFinalCats()) {
            idleFrame = entity.getIdleFrame();
            float x = entity.getX();
            float y = entity.getY();
            float w = idleFrame.getRegionWidth();
            float h = idleFrame.getRegionHeight();
            game.batch.draw(idleFrame, x, y, w / CatGame.PPM, h / CatGame.PPM);
        }
        renderer.render();
        player.draw(game.batch);
        game.batch.end();
       // b2dr.render(world, gameCamera.combined);

        if (player.dead)
            deadTimer += delta;
        if (deadTimer >= 3) {
            game.setScreen(new GameOverScreen(game));
        }
        if (player.finished) {
            game.setScreen( new Success(game));
        }
    }

    public World getWorld() {
        return this.world;
    }
    public TiledMap getMap() {
        return this.map;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();



    }
}
