package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class Controller {
    private static final int FIELD_WIDTH = 5;
    private static final int FIELD_HEIGHT = 5;
    private static final int SQUARE_SIZE = 120;
    private static final int SCREEN_WIDTH = FIELD_WIDTH * SQUARE_SIZE;
    private static final int SCREEN_HEIGHT = FIELD_HEIGHT * SQUARE_SIZE;
    private static final int WIN_COUNT = 3;
    private View view;
    private Graphics graphics;
    private State[][] states = new State[FIELD_WIDTH][FIELD_HEIGHT];
    private Set<Point> winPoints = new HashSet<>();
    private boolean isCrossTurn = true;

    public void setView(View view) {
        this.view = view;
    }

    public void start() {
        view.create(SCREEN_WIDTH, SCREEN_HEIGHT);
        initStates();
        render();
    }

    public void render() {
        BufferedImage image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();
        drawBounds();
        drawFigures();
        drawRedBounds();
        view.setImage(image);
    }

    private void drawBounds() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                draw(createSquare(Color.WHITE), i, j);
            }
        }
    }

    private void drawRedBounds() {
        for (Point p : winPoints) {
            draw(createSquare(Color.RED), p.x, p.y);
        }
    }

    private BufferedImage createSquare(Color color) {
        BufferedImage image = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(color);
        graphics.drawRect(0, 0, SQUARE_SIZE - 1, SQUARE_SIZE - 1);
        return image;
    }

    private BufferedImage createCross() {
        BufferedImage image = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.drawLine(0, 0, SQUARE_SIZE - 1, SQUARE_SIZE - 1);
        graphics.drawLine(SQUARE_SIZE - 1, 0, 0, SQUARE_SIZE - 1);
        return image;
    }

    private BufferedImage createCircle() {
        BufferedImage image = new BufferedImage(SQUARE_SIZE, SQUARE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.drawOval(0, 0, SQUARE_SIZE, SQUARE_SIZE);
        return image;
    }

    private void draw(BufferedImage image, int x, int y) {
        graphics.drawImage(image, x * SQUARE_SIZE, y * SQUARE_SIZE, null);
    }

    private void initStates() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                states[i][j] = State.EMPTY;
            }
        }
    }

    private void drawFigures() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                if (states[i][j] == State.CROSS) {
                    draw(createCross(), i, j);
                } else if (states[i][j] == State.CIRCLE) {
                    draw(createCircle(), i, j);
                }
            }
        }
    }

    private void checkLine(int xStart, int yStart, int dx, int dy) {
        State state = states[xStart][yStart];
        if (state == State.EMPTY) {
            return;
        }
        int cX = xStart;
        int cY = yStart;
        for (int i = 0; i < WIN_COUNT - 1; i++) {
            cX += dx;
            cY += dy;
            if (state != states[cX][cY]) {
                return;
            }
        }
        System.out.println("Victory");
        for (int i = 0; i < WIN_COUNT; i++) {
            winPoints.add(new Point(cX, cY));
            cX -= dx;
            cY -= dy;
        }
    }

    private void checkWin() {
        for (int i = 0; i <= FIELD_WIDTH - WIN_COUNT; i++) {
            for (int j = 0; j < FIELD_HEIGHT; j++) {
                checkLine(i, j, 1, 0);
            }
        }
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j <= FIELD_HEIGHT - WIN_COUNT; j++) {
                checkLine(i, j, 0, 1);
            }
        }
        for (int i = 0; i <= FIELD_WIDTH - WIN_COUNT; i++) {
            for (int j = 0; j <= FIELD_HEIGHT - WIN_COUNT; j++) {
                checkLine(i, j, 1, 1);
            }
        }
        for (int i = FIELD_WIDTH - WIN_COUNT; i < FIELD_WIDTH; i++) {
            for (int j = 0; j <= FIELD_HEIGHT - WIN_COUNT; j++) {
                checkLine(i, j, -1, 1);
            }
        }
    }

    public void handleMouseClick(int mouseX, int mouseY) {
        int x = mouseX / SQUARE_SIZE;
        int y = mouseY / SQUARE_SIZE;
        if (states[x][y] != State.EMPTY) {
            return;
        }
        states[x][y] = isCrossTurn ? State.CROSS : State.CIRCLE;
        isCrossTurn = !isCrossTurn;
        checkWin();
        render();
    }
}
