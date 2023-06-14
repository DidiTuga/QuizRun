package di.ubi.quizrun;

public class User {
    // "Pos", "Date", "Distancia", "Tempo, "Pontos", "Nº", "Nome", "Curso"
    private String pos;
    private String date;
    private String distancia;
    private String tempo;
    private String pontos;
    private String num;
    private String nome;
    private String curso;
    private boolean expanded;

    public User(String pos, String date, String distancia, String tempo, String pontos, String num, String nome, String curso) {
        this.pos = pos;
        this.date = date;
        this.distancia = distancia;
        this.tempo = tempo;
        this.pontos = pontos;
        this.num = num;
        this.nome = nome;
        this.curso = curso;
        this.expanded = false;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "User{" +
                "pos='" + pos + '\'' +
                ", date='" + date + '\'' +
                ", distancia='" + distancia + '\'' +
                ", tempo='" + tempo + '\'' +
                ", pontos='" + pontos + '\'' +
                ", num='" + num + '\'' +
                ", nome='" + nome + '\'' +
                ", curso='" + curso + '\'' +
                ", expanded=" + expanded +
                '}';
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getPontos() {
        return pontos;
    }

    public void setPontos(String pontos) {
        this.pontos = pontos;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
}
