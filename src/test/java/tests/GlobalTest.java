package tests;

import com.google.gson.Gson;
import io.thunder.utils.vson.tree.VsonTree;
import lombok.SneakyThrows;


public class GlobalTest {

    @SneakyThrows
    public static void main(String[] args) {


        VsonTree<Gson> gsonVsonTree = VsonTree.newTree(new Gson());


        System.out.println(gsonVsonTree.toVson());

    }
}
