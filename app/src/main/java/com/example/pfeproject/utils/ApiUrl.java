package com.example.pfeproject.utils;

public class ApiUrl {
    public static final String hostname = "http://192.168.1.12:8081";
    public static final String hostnameHost = "http://127.0.0.1:8081";

    //    ..signIn / singUp api
    public static final String apiSignUp = hostname + "/client/register";
    public static final String apiSignIn = hostname + "/login";

    //    ..Profile api
    public static final String apiGetUserData = hostname + "/client/"; //+userId
    public static final String apiUpdateUserData = hostname + "/client";
    public static final String apiUploadUserPic = hostname + "/uploadimage/"; //+userId

    //   ..Command and history api
    public static final String apiGetAllCmd = hostname + "/command";
    public static final String apiSaveUserCommand = hostname + "/command/client/"; //{{userId}}/product/?idp={{productId}}&idp={{productId}}.....

    //    ..Publicity and category api
    public static final String apiGetAllCategory = hostname + "/category";

    //    ..Store + product api
    public static final String apiGetProductById = hostname+"/product/"; //+productId;

    //    ..Point api
    public static final String apiGetEntrepriseById  = hostname+"/entreprise/"; //entrepriseId;
    public static final String apiAffectPointUserPublicity  = hostname+"/point/client/"; //{{userId}}/publicity/{{pubId}};

    //    method GET POST PUT DELETE ....
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";


}
