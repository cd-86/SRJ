//
// Created by User on 2024/3/3.
//

#ifndef SRJ_FONTFACE_H
#define SRJ_FONTFACE_H

#include <string>
#include <map>
#include "ft2build.h"
#include FT_FREETYPE_H


class FontFace {
public:
    struct TextureInfo {
        uint width;
        uint height;
        int bearingX;
        int bearingY;
        int advance;
        uint xOffset;
    };
    enum AlignmentFlag {
        AlignLeft = 0x0001,
        AlignRight = 0x0002,
        AlignHCenter = 0x0004,
        AlignTop = 0x0080,
        AlignBottom = 0x0010,
        AlignVCenter = 0x0020,
        AlignCenter = AlignVCenter | AlignHCenter
    };
    FontFace(FT_Byte *file_base, FT_Long file_size);

    ~FontFace();

    inline std::string getErrorString() const { return m_errorString; }
    inline uint getTextureWidth() const { return m_textureWidth; }
    inline uint getTextureHeight() const { return m_textureHeight; }
    inline FT_Bytes getTextureData() const { return m_textureData; }

    TextureInfo getCharInfo(FT_ULong c);

private:
    FT_Library m_ft;
    FT_Face m_face;
    std::string m_errorString;

    FT_Byte* m_textureData {nullptr};
    uint m_textureWidth {0};
    uint m_textureHeight {0};
};


#endif //SRJ_FONTFACE_H
