package 设计模式.结构型模式.桥接模式;

/**
 * @author like
 * @date 2020-12-21 15:27
 * @contactMe 980650920@qq.com
 * @description 扩展抽象化角色
 */
public class Mac extends OperatingSystem {

    public Mac(VideoFile videoFile) {
        super(videoFile);
    }

    @Override
    public void playVideo(String filename) {
        videoFile.deCode(filename);
    }
}
