package di.ubi.quizrun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH>{

    private ArrayList<User> users;
    private Context context;

    public UserAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartable, parent, false);
        return new UserVH((ViewGroup) view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        User user = users.get(position);
        if (position < 3) {
            if (position == 0) {
                holder.pos.setText("");
                holder.pos.setBackgroundResource(R.drawable.priplace);
            } else if (position == 1) {
                holder.pos.setText("");
                holder.pos.setBackgroundResource(R.drawable.segplace);
            } else if (position == 2) {
                holder.pos.setBackgroundResource(R.drawable.tercplace);
                holder.pos.setText("");
            }
        } else {
            // colocar o numero da posicao e retirar o background
            holder.pos.setText(user.getPos());
            holder.pos.setBackgroundResource(0);
        }
        holder.date.setText(user.getDate());
        holder.nome.setText(user.getNome());
        String pontos = context.getString(R.string.str_pontos) + user.getPontos();
        holder.pontos.setText(pontos);
        //-- AGORA VAI ADICIONAR UMA STRING A DIZER O QUE É ANTES
        String distancia = context.getString(R.string.Str_distancia)+" " + user.getDistancia();
        holder.distancia.setText(distancia);
        String tempo = context.getString(R.string.str_tempoDemorado)+" "  + user.getTempo();
        holder.tempo.setText(tempo);
        String num = context.getString(R.string.Str_Naluno)+ " " + user.getNum();
        holder.num.setText(num);
        String curso = context.getString(R.string.Str_curso)+": " + user.getCurso();
        holder.curso.setText(curso);

        boolean isExpanded = users.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserVH extends RecyclerView.ViewHolder{
        TextView pos, date, distancia, tempo, pontos, num, nome, curso;
        ConstraintLayout expandableLayout, notExpandableLayout;
        ImageView arrow, profile;

        public UserVH(@NonNull ViewGroup parent) {
            super(parent);
            pos = parent.findViewById(R.id.txt_pos);
            date = parent.findViewById(R.id.txt_data);
            distancia = parent.findViewById(R.id.txt_distancia);
            tempo = parent.findViewById(R.id.txt_tempo);
            pontos = parent.findViewById(R.id.txt_score);
            num = parent.findViewById(R.id.txt_numero);
            nome = parent.findViewById(R.id.txt_nome);
            curso = parent.findViewById(R.id.txt_curso);
            expandableLayout = parent.findViewById(R.id.expandableLayout);
            notExpandableLayout = parent.findViewById(R.id.not_expandableLayout);
            arrow = parent.findViewById(R.id.expandableButton);
            profile = parent.findViewById(R.id.IMG_profile);

            notExpandableLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = users.get(getAdapterPosition());
                    user.setExpanded(!user.isExpanded());
                    // muda a imagem do botão arrow_down_float para arrow_up_float
                    arrow.setImageResource(user.isExpanded() ? R.drawable.arrow_down : R.drawable.arrow_up);
                    notifyItemChanged(getAdapterPosition());

                }
            });

        }
    }
}
