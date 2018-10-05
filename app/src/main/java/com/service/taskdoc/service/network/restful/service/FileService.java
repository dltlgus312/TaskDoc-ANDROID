package com.service.taskdoc.service.network.restful.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.service.taskdoc.database.transfer.FileVO;
import com.service.taskdoc.service.network.restful.crud.FileCRUD;
import com.service.taskdoc.service.system.support.OnAttachmentDownloadListener;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.FileHandler;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileService {

    private final String TAG = "File";

    private NetworkSuccessWork networkSuccessWork;
    private FileCRUD service;

    private FileLoadService fileLoadService;

    private Handler handler;


    public FileService() {
        this.fileLoadService = fileLoadService;
        service = RequestBuilder.createService(FileCRUD.class);
        handler = new Handler(Looper.getMainLooper());
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public FileLoadService getFileLoadService() {
        return fileLoadService;
    }

    public void setFileLoadService(FileLoadService fileLoadService) {
        this.fileLoadService = fileLoadService;
    }

    public void list(int dmcode) {
        Call<List<FileVO>> request = service.getFileList(dmcode);
        request.enqueue(new Callback<List<FileVO>>() {
            @Override
            public void onResponse(Call<List<FileVO>> call, Response<List<FileVO>> response) {

                if (response.body().size() > 0 && response.body() != null) {
                    List<FileVO> list = response.body();
                    networkSuccessWork.work(list);
                }
            }

            @Override
            public void onFailure(Call<List<FileVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(int fcode) {
        Call<Integer> request = service.deleteFile(fcode);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1)
                    networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void download(FileVO vo) {
        Call<ResponseBody> request = service.downloadFile(vo.getFcode());
        fileLoadService.start(vo);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            writeResponseBodyToDisk(response.body(), vo.getFname());
                        }
                    });
                    t.start();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                fileLoadService.fail(t.getMessage());
            }
        });
    }


    /*
     * Stored
     * */

    private void writeResponseBodyToDisk(ResponseBody body, String fname) {
        try {
            // todo change the file location/name according to your needs
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Download", fname);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            file.createNewFile();

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    if (fileLoadService != null) {
                        long finalFileSizeDownloaded = fileSizeDownloaded;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                fileLoadService.update(finalFileSizeDownloaded, fileSize);
                            }
                        });
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }


                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fileLoadService.fail("파일 저장 실패");
                    }
                });
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fileLoadService.end();

                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    fileLoadService.fail("파일 저장 실패");
                }
            });
        }
    }


    public interface FileLoadService {
        void fail(String msg);

        void start(FileVO vo);

        void update(long percent, long total);

        void end();
    }

}
