package me.wcy.music.presenter.interfaceP;

import me.wcy.music.model.Params;
import me.wcy.music.model.User;

public interface IUserP {
    void login(Params[] params);

    void register(Params params);

    void update(Params[] params);

    void updataUser(Params params);

    void getUser(Params params);

    void getUserByID(Params params);

    void getFriend(Params params);
}
