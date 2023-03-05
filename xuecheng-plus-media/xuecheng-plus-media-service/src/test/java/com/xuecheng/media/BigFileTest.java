package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/2/28 22:16
 */
public class BigFileTest {


    // 将文件分块上传
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourceFile = new File("O:\\bigfile_test\\bear.mp4");

        //分块文件存储路径
        File chunkFolderPath = new File("O:\\bigfile_test\\chunk\\");
        if(!chunkFolderPath.exists()){
            chunkFolderPath.mkdirs();
        }

        //定义分块的大小
        int chunkSize = 1024 * 1024 * 1;

        //分块的数量
        long chunkNum = (long)Math.ceil(sourceFile.length() * 1.0 / chunkSize);

        //使用流对象读取文件，向分块文件写数据，达到分块大小就停止

        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");

        //缓冲区
        byte[] bytes = new byte[1024];
        for(long i = 0; i<chunkNum;i++){
            File file = new File("O:\\bigfile_test\\chunk\\" + i);

            if(file.exists()) file.delete(); //如果分块文件存在则删除
            //向分块分件写数据流对象
            boolean newFile = file.createNewFile();
            if(newFile){

                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");

                int len = -1;
                while((len = raf_read.read(bytes))!= -1){
                    //向文件写入数据
                    raf_write.write(bytes,0,len);
                    if(file.length()>=chunkSize) break;
                }
                raf_write.close();
            }
        }
        raf_read.close();
    }

    // 合并分块文件
    @Test
    public void testMerge() throws IOException{
        //源文件
        File sourceFile = new File("O:\\bigfile_test\\bear.mp4");

        //分块文件存储路径
        File chunkFolderPath = new File("O:\\bigfile_test\\chunk\\");
        if(!chunkFolderPath.mkdirs()){
            chunkFolderPath.mkdirs();
        }
        //合并后的文件
        File mergeFile = new File("O:\\bigfile_test\\bear_01.mp4");
        boolean newFile = mergeFile.createNewFile();

        //思路，使用流对象读取分块分件，按顺序将分块文件依次向合并文件写数据
        //获取分块文件列表，按文件名升序排序
        File[] chunkFiles = chunkFolderPath.listFiles();
        List<File> chunkFileList = Arrays.asList(chunkFiles);
        Collections.sort(chunkFileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //创建合并文件的流对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        byte[] bytes = new byte[1024];
        for(File file : chunkFileList){
            //读取分块文件的流程
            RandomAccessFile raf_read = new RandomAccessFile(file,"r");
            int len = -1;
            while((len = raf_read.read(bytes))!=1){
                raf_write.write(bytes,0,len);
            }
        }

        //校验合并后的文件是否正确
        FileInputStream sourceFileStream = new FileInputStream(sourceFile);
        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        String sourceMd5Hex = DigestUtils.md5Hex(sourceFileStream);
        String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);

        if(sourceMd5Hex.equals(mergeMd5Hex)){
            System.out.println("合并成功");
        }


    }
}
