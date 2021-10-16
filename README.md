# comp90018-assignment2 Good OG
[![license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/Blackmesa-Canteen/comp90018-assignment2/dev/LICENSE?token=AQUQBSROD65KMHOLRQ2CK3TBNFGJ6)
[![platform](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com)

*This is an assignment for subject Comp90018 in the University of Melbourne.*

## Introduction
Good OG is a second-hand mall APP for android. It supports product browsing, searching, publishing, instant messaging, voice messaging, positioning.
A user can publish second-hand products for others to buy, while he can also buy from theothers.

## Tech stack

- Client: Android Native (Java).
- Backend: Google Firebase, JMessage IM server, BaiduMap API, openStreetMap API.

## DEMO
gif/screenshot

## Features
- Product info viewing/product detials displaying;
- Nearby product displaying;
- Product categories;
- Product name searching;
- Product management: publish, cancel, pend and edit;
- Order management: make an order, monitor order, manipulate order states;
- Order feedback;
- Authentication: Register/login
- Profile management: Edit info/password/avatar
- IM chat: Text, emoji, image, video, voice and location sharing;
- Location service: Map displying, antigeocoding, decrypt coordinates in mainland China;
- Media service: Gallery, Camera, Microphone.


## Credits
### Thrid party dependencies
1. [Android-PromptDialog](https://github.com/limxing/Android-PromptDialog): For more convient dialog displaying.
2. [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper): Simplify coding RecyclerView's adapters.
3. [BaiduMap SDK](https://lbsyun.baidu.com): For Mapview displaying.
4. [CircleImageView](https://github.com/hdodenhof/CircleImageView): Suitable for avatar displaying.
5. [FastJson](https://github.com/alibaba/fastjson): Used for parsing JSON body from Open streetmap antigeocoding API.
6. [Jmessage SDK](https://docs.jiguang.cn/jmessage/guideline/jmessage_guide/): Third party Free IM chat backend server.
7. [LabelsView](https://github.com/donkingliang/LabelsView): For label UI displaying.
8. [Lombok](https://github.com/projectlombok/lombok): Simplify coding pojos/beans/models.
9. [NavigationTabStrip](https://github.com/Devlight/NavigationTabStrip): UI element for tab displaying.
10. [OkHttp](https://github.com/square/okhttp): A framework for http requesting.
11. [OpenStreetMap](https://wiki.openstreetmap.org/wiki/Main_Page): A free geo service API, we used it for antigeocoding i.e. retrieve text addresses from coordanates.
12. [PictureSelector](https://github.com/LuckSiege/PictureSelector): Easy image resource selector.
13. [WaveSwipeRefreshLayout](https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout): UI element for Pull-down refreshing.
14. [supernova-emoji-library](https://github.com/hani-momanii/SuperNova-Emoji): Resources for displaying emoji.
15. [yjPlay](https://github.com/yangchaojiang/yjPlay): For video playing in IM.


# License
```
MIT License

Copyright (c) 2021 996worker & erichong0815 & Ricky-Xu-UOW & lengary1110 & jzzh4 & kkk-1518

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

