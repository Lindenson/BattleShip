package wolper.domain;


import lombok.Data;

@Data
public class BoardOfShips {

    private Ships[][] shipLines = new Ships[10][10];
    public record Ships(int x, int y, int staT, int pos, int siZe, int commonGranz, int id) {}
}


