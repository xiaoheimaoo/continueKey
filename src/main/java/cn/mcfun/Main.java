package cn.mcfun;

import cn.mcfun.utils.EncryptFile;

public class Main {
    public static void main(String[] args) {
        EncryptFile res = new EncryptFile();
        System.out.println(res.getFile(""));
    }
}
