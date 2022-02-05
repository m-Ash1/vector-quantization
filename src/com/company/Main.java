package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Raster raster;

    // read image and convert it to 2d array
    static double [] [] read_image () {
        File file = new File("gray.jpeg");
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int width = img.getWidth();
        int height = img.getHeight();
        double[][] arr = new double[width][height];

        raster = img.getData();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                arr[i][j] = raster.getSample(i, j, 0);

            }
        }
        return  arr;
    }

    public static int log2(int x) {
        return (int) (Math.log(x) / Math.log(2));
    }

    public static void main(String[] args) throws IOException {

        double [] [] matrix = read_image();

        System.out.println("Please enter vector size: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] chars = input.split("\\*");

        int h1 = Integer.parseInt(chars[0]);
        int h2 = Integer.parseInt(chars[1]);

        System.out.println("Please Enter size of code book: ");
        int codebook_size = scanner.nextInt();

        //Compress

        Vector_quan v =new Vector_quan(h1,h2,codebook_size);

        // split into small vectors
        ArrayList<Vector_quan.struct> arrayList = v.splitmatrix(matrix);

        // get average of vectors
        ArrayList<Vector_quan.struct> average = v.average(arrayList);
        ArrayList<Vector_quan.struct> vectors = new ArrayList<>();

        for(int i=0;i<log2(codebook_size);i++)
        {
            // split each vector into two vectors
            vectors = v.split(average);

            // compare vector with vectors
            v.compare(arrayList,vectors);

            // get average for two vectors
            average = v.average(vectors);
        }

        // assign each vector for nearest vector
        v.compare(arrayList,average);

        // set codebook
        v.setCodeBook(average);

        //compress image to code
        v.compress_image(arrayList);

        //Decompress

        // get all vectors of image
        ArrayList<double[][]> decompress = v.Decompress();

        // build matrix of vectors to decompress image
        double[][]output = v.create_matrix(decompress,matrix.length,matrix[0].length);

        // decompress image
        Write_image(output);
    }

    static void Write_image(double[][] arr) throws IOException {
        int xLenght = arr.length;
        int yLength = arr[0].length;
        BufferedImage b = new BufferedImage(xLenght, yLength, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < xLenght; x++) {
            for(int y = 0; y < yLength; y++) {
                int rgb = (int)arr[x][y]<<16 | (int)arr[x][y] << 8 | (int)arr[x][y];
                b.setRGB(x, y, rgb);
            }
        }
        ImageIO.write(b, "jpg", new File("output.jpg"));
        System.out.println("Image Decompress Complete");
    }
}