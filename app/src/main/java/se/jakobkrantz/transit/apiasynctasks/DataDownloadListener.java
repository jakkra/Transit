package se.jakobkrantz.transit.apiasynctasks;
/*
 * Created by jakkra on 2015-03-02.
 */

public interface DataDownloadListener {
    void dataDownloadedSuccessfully(Object data);

    void dataDownloadFailed();
}