package org.example.logics;

enum SnakeState {
    ALIVE,  // Змея управляется игроком
    ZOMBIE // Змея принадлежала игроку, который вышел из игры, она продолжает движение куда глаза глядят
}
