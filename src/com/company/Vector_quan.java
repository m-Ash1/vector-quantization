package com.company;

import java.util.ArrayList;


public class Vector_quan {
    static class struct
    {
        double[][] vector;
        ArrayList<double[][]> array = new ArrayList<>();
        String code;
        public struct(double [][]v)
        {
            vector=v;
        }
    }

    ArrayList<struct> vec = new ArrayList<>();
    int sizew,sizeh;
    String compress_code="";
    int codeBookSize;
    ArrayList<struct> CodeBook = new ArrayList<>();

    // set codebook
    public void setCodeBook(ArrayList<struct> codeBook) {
        CodeBook = codeBook;
        assign_code(CodeBook,codeBookSize);
    }

    //assign code for each vector
    public void assign_code(ArrayList<struct> codeBook,int size)
    {
        int code=0;
        for (int i=0;i<size;i++)
        {
            if(Integer.toBinaryString(code).length()==Main.log2(codeBookSize)) //3
                codeBook.get(i).code=Integer.toBinaryString(code);
            else
            {
                String s="";
                for(int j=0;j<Main.log2(codeBookSize)-Integer.toBinaryString(code).length();j++)
                    s+="0";
                codeBook.get(i).code=s+Integer.toBinaryString(code);
            }
            code++;
        }
    }

    // to get compress code
    public void compress_image(ArrayList<struct> arr)
    {
        for(int i=0;i<arr.size();i++)
        {
            for(int j=0;j<arr.get(i).array.size();j++)
            {
                for(int k=0;k<CodeBook.size();k++)
                {
                    if(CodeBook.get(k).array.contains(arr.get(i).array.get(j)))
                    {
                        compress_code+=CodeBook.get(k).code;
                        break;
                    }
                }
            }
        }
        System.out.println("Image Compressed");
    }

    // constructor
    public Vector_quan(int sizew, int sizeh, int codeBookSize) {
        this.sizew = sizew;
        this.sizeh = sizeh;
        this.codeBookSize = codeBookSize;
    }

    // split matrix into vectors
    public  ArrayList<struct> splitmatrix(double [] [] m) {
        double[][] vector = new double[sizew] [sizeh];
        struct v = new struct(null);
        ArrayList<struct> vectorList = new ArrayList<>();
        int wid=0,high=0,countw=0,counth=0;
        for (int i = 0; i < m.length*m[0].length; i++) {
            if(wid<sizew)
            {
                // adding elements horizontally
                if(high<sizeh)
                {
                    vector[wid][high]=m[wid+countw][high+counth];
                    high++;
                }
                else
                {
                    // adding elements vertically
                    wid++;

                    if(wid<sizew)
                    {
                        high=0;
                        vector[wid][high]=m[wid+countw][high+counth];
                        high++;
                    }
                    else
                    {
                        counth+=high;
                        if(counth==m[0].length)
                        {
                            counth=0;
                            countw+=wid;
                        }
                        wid=0;
                        high=0;
                        v.array.add(vector);
                        vector = new double[sizew][sizeh];
                        if(counth!=m[0].length&&countw!=m.length)
                        {
                            vector[wid][high]=m[wid+countw][high+counth];
                            high++;
                        }
                    }

                }
            }

        }
        v.array.add((vector));
        vectorList.add(v);
        return vectorList;
    }

    // get vectors from code book
    public ArrayList<double[][]> Decompress()
    {
        ArrayList<double[][]> array = new ArrayList<>();
        String s="";
        for(int i=0;i<compress_code.length();i++)
        {
            s+=compress_code.charAt(i);
            for(int j=0;j<codeBookSize;j++)
            {
                if(CodeBook.get(j).code.equalsIgnoreCase(s))
                {
                    array.add(CodeBook.get(j).vector);
                    s="";
                    break;
                }
            }
        }
        return array;
    }

    // create matrix again to decompress into image
    public double[][] create_matrix(ArrayList<double[][]> array,int row,int col)
    {
        double [] [] result = new double[row][col];
        int mid=0,high=0,index=0,kk;
        for(int i=0;i<row;i++)
        {
            kk=index;
            for(int j=0;j<col;j++)
            {
                if(high<sizeh)
                {
                    result[i][j]=array.get(index)[mid][high];
                    high++;
                }
                else
                {
                    index++;
                    high=0;
                    result[i][j]=array.get(index)[mid][high];
                    high++;
                }
            }
            mid++;
            if(mid<sizew)
            {
                index=kk;
                high=0;
            }
            else{
                mid=mid%sizew;
                index++;
                high=0;
            }

        }
        return result;
    }

    // split vector into two vectors
    public  ArrayList<struct> split (ArrayList<struct> m)
    {
        double [][] vect = new double [sizew][sizeh];
        ArrayList<struct> array = new ArrayList<>();
        for(int k=0;k<m.size();k++)
        {
            for(int i=0;i<m.get(k).vector.length;i++)
            {
                for(int j=0;j<m.get(k).vector[i].length;j++)
                {
                    vect[i][j] = (int) Math.ceil(m.get(k).vector[i][j]);
                    if(vect[i][j]==m.get(k).vector[i][j])
                        vect[i][j]++;

                }
            }
            array.add(new struct(vect));
            vect = new double [sizew][sizeh];

            for(int i=0;i<m.get(k).vector.length;i++)
            {
                for(int j=0;j<m.get(k).vector[i].length;j++)
                {
                    vect[i][j] = (int) Math.floor(m.get(k).vector[i][j]);
                    if(vect[i][j]==m.get(k).vector[i][j])
                        vect[i][j]--;

                }
            }
            array.add(new struct(vect));
            vect = new double [sizew][sizeh];
        }
        return array;
    }

    // get average of vector
    public  ArrayList<struct> average(ArrayList<struct> vectorlist) {
        double[][] result = new double[sizew][sizeh];
        ArrayList<struct> list = new ArrayList<>();
        for(int i=0;i<vectorlist.size();i++)
        {
            for (int a=0;a<vectorlist.get(i).array.size();a++)
            {
                for(int j=0;j<sizew;j++) {
                    for (int k=0;k<sizeh;k++) {
                        result[j][k] += vectorlist.get(i).array.get(a)[j][k];
                    }
                }
            }
            for (int j = 0; j < sizew; j++) {
                for (int k = 0; k < sizeh; k++) {
                    result[j][k] /= vectorlist.get(i).array.size();
                }
            }
            if(vectorlist.get(i).array.size()==0)
            {
                list.add(new struct(vectorlist.get(i).vector));
                result = new double[sizew][sizeh];
            }
            else
            {
                list.add(new struct(result));
                result = new double[sizew][sizeh];
            }

        }
        return list;
    }

    //calculate square error
    public  int SE(double[][] a,double[][] b)
    {
        int mse=0;
        for(int i=0;i<a.length;i++)
        {
            for(int j=0;j<a[i].length;j++)
            {
                mse+=Math.pow(a[i][j]-b[i][j],2);
            }
        }
        return mse;
    }

    public void compare(ArrayList<struct> source, ArrayList<struct> dest)
    {
        for(int i=0;i<source.get(0).array.size();i++)
        {
            int min= (int) 1e9;
            int index=-1;
            for(int j=0;j<dest.size();j++)
            {
                int m = SE(source.get(0).array.get(i),dest.get(j).vector);
                if(min>m)
                {
                    min=m;
                    index=j;
                }
            }
            dest.get(index).array.add(source.get(0).array.get(i));
        }
    }

}
