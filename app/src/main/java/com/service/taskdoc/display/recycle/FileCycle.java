package com.service.taskdoc.display.recycle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.FileVO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileCycle extends RecyclerView.Adapter<FileCycle.ViewHolder> {

    private String path;

    private File file;
    private List<Item> list;
    private List<List<Item>> stackList;

    private List<FileVO> vos;

    boolean empty = false;


    private OnClickListener onClickListener;
    private OnClickDownload onClickDownload;


    public FileCycle() {

        list = new ArrayList<>();
        stackList = new ArrayList<>();
        String rootSD = Environment.getExternalStorageDirectory().toString();
        file = new File(rootSD);
        setFileList(file);

    }

    public FileCycle(List<Item> list) {
        this.list = list;
    }

    public FileCycle(List<FileVO> vos, boolean empty) {
        this.vos = vos;
        this.empty = empty;
    }


    public FileCycle(String path) {

        list = new ArrayList<>();
        stackList = new ArrayList<>();
        String rootSD = Environment.getExternalStorageDirectory().toString();
        file = new File(rootSD + "/" + path);
        setFileList(file);
    }


    protected void setFileList(File file) {

        File list[] = file.listFiles();

        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                Item item = new Item();
                item.setName(list[i].getName());
                if (list[i].getName().contains(".")) {
                    item.setImg(2);
                } else {
                    item.setImg(1);
                }
                item.setPath(file.getPath());
                this.list.add(item);
            }
        } else {
            Item item = new Item();
            item.setName(file.getName());
            if (item.getName().contains(".")) {
                item.setImg(2);
            } else {
                item.setImg(1);
            }
            item.setPath(file.getPath());
            this.list.add(item);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Item item = list.get(i);


        LinearLayout linearLayout = viewHolder.linearLayout;
        ImageView img = viewHolder.img;
        TextView name = viewHolder.name;


        if (item.getImg() == 1) {
            img.setImageResource(R.drawable.img_folder_fill);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null) onClickListener.folderClick(item);
                }
            });
        } else if (item.getImg() == 2) {
            img.setImageResource(R.drawable.img_file_doc);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null) onClickListener.fileClick(item);
                    if (onClickDownload != null) onClickDownload.downloadFile(vos.get(i));
                    notifyDataSetChanged();
                }
            });
        } else {
            img.setImageResource(R.drawable.img_file_check_doc);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null) onClickListener.fileClick(item);
                    notifyDataSetChanged();
                }
            });
        }

        name.setText(item.getName());

    }

    @Override
    public int getItemCount() {
        if (empty){
            List<Item> list = new ArrayList<>();

            for (FileVO vo : vos) {
                Item item = new Item();

                item.setName(vo.getFname());
                item.setImg(2);
                item.setFcode(vo.getFcode());

                list.add(item);
            }
            this.list = list;
        }
        return list.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_file, viewGroup, false);
        return new ViewHolder(view);
    }


    public void go(String path) {
        List<Item> ref = new ArrayList<>();
        ref.addAll(list);

        stackList.add(ref);

        list.clear();

        file = new File(file.getPath() + "/" + path);
        setFileList(file);
        notifyDataSetChanged();
    }

    public boolean canGoBack() {
        if (stackList.size() > 0) {
            return true;
        } else return false;
    }

    public void back() {
        list.clear();

        list.addAll(stackList.get(stackList.size() - 1));

        stackList.remove(stackList.size() - 1);

        String[] pathList = file.getPath().split("/");
        String path = "";
        for (int i = 0; i < pathList.length - 1; i++) {
            String p = pathList[i];
            path += p + "/";
        }
        path = path.substring(0, path.length() - 1);
        file = new File(path);
        notifyDataSetChanged();
    }

    public List getSelectList() {
        List<Item> items = new ArrayList<>();

        for (List<Item> list : stackList) {
            for (Item item : list) {
                if (item.getImg() == 3) {
                    items.add(item);
                }
            }
        }

        for (Item item : list) {
            if (item.getImg() == 3) {
                items.add(item);
            }
        }

        return items;
    }


    public OnClickDownload getOnClickDownload() {
        return onClickDownload;
    }

    public void setOnClickDownload(OnClickDownload onClickDownload) {
        this.onClickDownload = onClickDownload;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private ImageView img;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
        }
    }


    public interface OnClickListener {
        void folderClick(Item item);

        void fileClick(Item item);
    }

    public interface OnClickDownload{
        void downloadFile(FileVO vo);
    }

    public class Item {
        String path;
        String name;
        int img;
        int fcode;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getFcode() {
            return fcode;
        }

        public void setFcode(int fcode) {
            this.fcode = fcode;
        }
    }
}
