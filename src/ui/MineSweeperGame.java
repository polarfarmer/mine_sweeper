package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MineSweeperGame extends JFrame {
    private JButton[][] buttons;
    private boolean[][] mines;
    private boolean[][] flags;
    private int[][] numbers;
    private final int GRID_SIZE = 10;
    private final int BUTTON_SIZE = 45;
    private final int TOTAL_MINES = 15; // 총 지뢰 개수
    private boolean[][] breaks;
    private boolean gameOver = false;

    public MineSweeperGame() {
        setTitle("지뢰찾기");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        // 배열 초기화
        buttons = new JButton[GRID_SIZE][GRID_SIZE];
        mines = new boolean[GRID_SIZE][GRID_SIZE];
        flags = new boolean[GRID_SIZE][GRID_SIZE];
        breaks = new boolean[GRID_SIZE][GRID_SIZE];
        numbers = new int[GRID_SIZE][GRID_SIZE];

        // 지뢰 배치 및 숫자 계산
        placeMines();
        calculateNumbers();

        // 버튼 생성 및 이벤트 설정
        createButtons();

        pack();
        setLocationRelativeTo(null);
    }

    private void createButtons() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                button.setFont(new Font("Arial", Font.BOLD, 16));

                final int currentRow = row;
                final int currentCol = col;

                // 마우스 이벤트 처리
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (gameOver) return;

                        // 좌클릭
                        if (e.getButton() == MouseEvent.BUTTON1 && !flags[currentRow][currentCol]) {
                            handleLeftClick(currentRow, currentCol);
                        }
                        // 우클릭
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            handleRightClick(currentRow, currentCol);
                        }
                    }
                });

                buttons[row][col] = button;
                add(button);
                buttons[row][col].setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    private void placeMines() {
        Random random = new Random();

        for (int i = 0; i < TOTAL_MINES; i++) {
            int x = random.nextInt(GRID_SIZE);
            int y = random.nextInt(GRID_SIZE);
            if (!mines[x][y]) {
                mines[x][y] = true;
            }
            else {
                i -= 1;
            }
        }

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                System.out.print((mines[i][j] ? "1" : "0") + ", ");
            }
            System.out.println();
        }
    }

    private void calculateNumbers() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (!mines[row][col]) {
                    numbers[row][col] = countAdjacentMines(row, col);
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < GRID_SIZE &&
                        newCol >= 0 && newCol < GRID_SIZE &&
                        mines[newRow][newCol]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void handleLeftClick(int row, int col) {
        if (buttons[row][col].isEnabled() && !flags[row][col]) {
            if (mines[row][col]) {
                gameOver = true;
                revealAllMines();
                JOptionPane.showMessageDialog(this, "게임 오버!");
            } else {
                reveal(row, col);
            }
        }
    }

    private void handleRightClick(int row, int col) {
        if (buttons[row][col].isEnabled() && !flags[row][col] && !breaks[row][col]) {
            buttons[row][col].setBackground(Color.green);
            flags[row][col] = true;
        }else if (buttons[row][col].isEnabled() && flags[row][col] && !breaks[row][col]){
            buttons[row][col].setBackground(Color.LIGHT_GRAY);
            flags[row][col] = false;
        }
    }

    private void reveal(int row, int col) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE ||
                !buttons[row][col].isEnabled() || flags[row][col]) {
            return;
        }
        breaks[row][col] = true;
        buttons[row][col].setBackground(Color.white);
        if (numbers[row][col] > 0) {
            buttons[row][col].setText(String.valueOf(numbers[row][col]));
            setNumberColor(buttons[row][col], numbers[row][col]);
        } else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    reveal2(row + i, col + j);
                }
            }
        }
    }
    private void reveal2(int row, int col) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE ||
                !buttons[row][col].isEnabled() || flags[row][col]) {
            return;
        }
        breaks[row][col] = true;
        buttons[row][col].setBackground(Color.white);
        if (numbers[row][col] > 0) {
            buttons[row][col].setText(String.valueOf(numbers[row][col]));
            setNumberColor(buttons[row][col], numbers[row][col]);
        }
    }

    private void setNumberColor(JButton button, int number) {
        Color[] colors = {
                null,
                Color.BLUE,
                new Color(0, 128, 0),
                Color.RED,         // 3
                new Color(0, 0, 128),
                new Color(128, 0, 0),
                new Color(128, 0, 128),
                Color.BLACK,
                Color.GRAY
        };
        button.setForeground(colors[number]);
    }

    private void revealAllMines() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (mines[i][j] && !flags[i][j]) {
                    buttons[i][j].setBackground(Color.red);
                    buttons[i][j].setEnabled(false);
                } else if (mines[i][j] && flags[i][j]) {
                    buttons[i][j].setBackground(Color.blue);
                    buttons[i][j].setEnabled(false);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MineSweeperGame().setVisible(true);
        });
    }
}