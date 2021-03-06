/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bigdecision;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import ttt.james.server.TTTWebService;
import ttt.james.server.TTTWebService_Service;

/**
 *
 * @author Colm
 */
public class GameScreenJFrame extends javax.swing.JFrame {

    private final TTTWebService service = new TTTWebService_Service().getTTTWebServicePort();
    private final Timer timer = new Timer();
    private final MainCoordinator coordinator;
    private final int userId;
    private final int gameId;
    private final int ROWS = 3;
    private final int COLUMNS = 3;
    private final JButton[][] gameButtons = new JButton[ROWS][COLUMNS];
    private boolean isMyTurn = true;

    /**
     * Creates new form GameScreenJFrame
     *
     * @param coordinator
     * @param userId
     * @param gameId
     */
    public GameScreenJFrame(MainCoordinator coordinator, int userId, int gameId) {
        this.coordinator = coordinator;
        this.userId = userId;
        this.gameId = gameId;
        initComponents();
        initGameBoard();
        setLocationRelativeTo(null);
        startTimer();
    }

    private void initGameBoard() {
        GridLayout layout = new GridLayout(ROWS, COLUMNS);
        boardPanel.setLayout(layout);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                final int row = i;
                final int column = j;
                JButton button = new JButton();
                button.addActionListener(e -> onButtonPressed(row, column));
                boardPanel.add(button);
                gameButtons[row][column] = button;
            }
        }
        boardPanel.setPreferredSize(new Dimension(240, 240));
    }

    private void onButtonPressed(int row, int column) {
        if (!isMyTurn) {
            JOptionPane.showMessageDialog(null, "It's not your turn!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        service.takeSquare(column, row, gameId, userId);
        updateGameBoard();
    }

    private void startTimer() {
        int interval = 1000;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateGameBoard();
            }
        };
        timer.scheduleAtFixedRate(task, 0, interval);
    }

    private void updateGameBoard() {
        String response = service.getBoard(gameId);
        if (response.equals(ErrorCodes.NO_MOVES) || response.equals(ErrorCodes.DB)) {
            return;
        }

        String[] moves = response.split("\n");

        //Check who made the last move so we don't make 2 in a row
        int lastMovePlayerId = Integer.parseInt(moves[moves.length - 1].split(",")[0]);
        isMyTurn = lastMovePlayerId != userId;

        for (String move : moves) {
            String[] components = move.split(",");
            int playerId = Integer.parseInt(components[0]);
            int column = Integer.parseInt(components[1]);
            int row = Integer.parseInt(components[2]);

            String marker = playerId == userId ? "X" : "O";
            gameButtons[row][column].setText(marker);
            gameButtons[row][column].setEnabled(false);
        }
        checkForWin();
    }

    private void checkForWin() {
        String response = service.checkWin(gameId);
        if (response.equals(ErrorCodes.RETRIEVE) || response.equals(ErrorCodes.DB) || response.equals(ErrorCodes.NO_GAME)) {
            return;
        }

        int gameState = Integer.parseInt(response);
        String gameResult;
        switch (gameState) {
            case 1:
                gameResult = "Player 1 has won!";
                service.setGameState(gameId, gameState);
                break;
            case 2:
                gameResult = "Player 2 has won!";
                service.setGameState(gameId, gameState);
                break;
            case 3:
                gameResult = "It's a draw!";
                service.setGameState(gameId, gameState);
                break;
            default:
                return;
        }
        //If we reach this point, the game is over
        timer.cancel();
        disableButtons();
        JOptionPane.showMessageDialog(null, gameResult, "Game Finished", JOptionPane.INFORMATION_MESSAGE);
    }

    private void disableButtons() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                gameButtons[i][j].setEnabled(false);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        boardPanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout boardPanelLayout = new javax.swing.GroupLayout(boardPanel);
        boardPanel.setLayout(boardPanelLayout);
        boardPanelLayout.setHorizontalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        boardPanelLayout.setVerticalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 248, Short.MAX_VALUE)
        );

        backButton.setLabel("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(boardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backButton)
                        .addGap(0, 325, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(boardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(backButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        timer.cancel();
        coordinator.goToMainMenu(userId);
    }//GEN-LAST:event_backButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel boardPanel;
    // End of variables declaration//GEN-END:variables
}
